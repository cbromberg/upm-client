package com.k15t.cloud.upm_client.jaxrs;

import com.k15t.cloud.upm_client.UpmClient;
import com.k15t.cloud.upm_client.UpmClientDetails;
import com.k15t.cloud.upm_client.UpmTaskUtil;
import org.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.Base64;
import java.util.Optional;


public class JaxRsUpmClient implements UpmClient {


    private final Client client;
    private final ProductAccess productAccess;
    private final long taskTimeout = Optional.ofNullable(System.getenv("UPM_CLIENT_INSTALL_TIMEOUT")).map(Long::parseLong).orElse(300000L);


    public JaxRsUpmClient(Client client, ProductAccess productAccess) {
        this.client = client;
        this.productAccess = productAccess;
    }


    @Override
    public void install(String appUrl) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())));
        String upmToken = getUpmToken(upmEndpoint);
        install(upmEndpoint.queryParam(UpmClientDetails.QUERY_PARAM_TOKEN, upmToken), appUrl);
    }


    @Override
    public <T> T setLicenseToken(String appKey, String tokenValue, TokenState tokenState, Class<T> type) {
        return applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())).path(UpmClientDetails.LICENSE_TOKEN_URL_PATH))
                .request()
                .post(Entity.entity(String.format(UpmClientDetails.TOKEN_JSON_PAYLOAD, appKey, tokenValue, tokenState.name()),
                        UpmClientDetails.CONTENT_TYPE_INSTALL_TOKEN_JSON), type);
    }


    @Override
    public void removeLicenseToken(String appKey) {
        applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())))
                .path(UpmClientDetails.LICENSE_TOKEN_URL_PATH)
                .path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null.")))
                .request()
                .delete(String.class);
    }


    protected String getUpmUrl(String productUrl) {
        return String.format("%s%s", productUrl, UpmClientDetails.ENDPOINT_URL_PATH);
    }


    @Override
    public void uninstall(String appKey) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())));
        Response uninstallResponse = upmEndpoint.path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null")))
                .request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS)
                .delete();
        Optional<WebApplicationException> error =
                getExceptionIfNotExpectedStatusCode(uninstallResponse, Response.Status.NO_CONTENT.getStatusCode());
        if (error.isPresent()) {
            if (error.get().getResponse().getStatus() == 403 && error.get().getMessage()
                    .contains(UpmClientDetails.ResponseCodes.UNINSTALLABLE)) {
                removeLicenseToken(appKey);
                uninstallResponse = upmEndpoint.path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null")))
                        .request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS)
                        .delete();
                getExceptionIfNotExpectedStatusCode(uninstallResponse, Response.Status.NO_CONTENT.getStatusCode()).ifPresent(e -> {
                    throw e;
                });
            } else {
                throw error.get();
            }
        }
    }


    @Override
    public <T> Optional<T> get(String appKey, Class<T> type) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())))
                .path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null.")));
        try {
            return Optional.of(upmEndpoint.request().get(type));
        } catch (WebApplicationException a) {
            if (a.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return Optional.empty();
            }
            throw a;
        }
    }


    @Override
    public <T> Optional<T> getLicenseToken(String appKey, Class<T> type) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(productAccess.getProductUrl())))
                .path(UpmClientDetails.LICENSE_TOKEN_URL_PATH)
                .path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null.")));
        try {
            return Optional.of(upmEndpoint.request().get(type));
        } catch (WebApplicationException a) {
            if (a.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return Optional.empty();
            }
            throw a;
        }
    }


    protected Optional<WebApplicationException> getExceptionIfNotExpectedStatusCode(Response response, int statusCode) {
        return Optional.of(response).filter(r -> r.getStatus() != statusCode)
                .map(response1 -> new WebApplicationException(
                        String.format("Expected %s, but got %s: %s ", statusCode, response.getStatus(), response.readEntity(String.class)),
                        response));
    }


    protected WebTarget applyAuthentication(WebTarget toAuthenticate) {
        if (this.productAccess == null) {
            throw new IllegalArgumentException("No productAccess given");
        }
        return toAuthenticate.register(
                (ClientRequestFilter) requestContext -> requestContext.getHeaders()
                        .putSingle("Authorization", "Basic " + Base64.getEncoder()
                                .encodeToString((this.productAccess.getUsername() + ":" + this.productAccess.getApiToken()).getBytes())));
    }


    protected final String requireNonBlank(String toCheck, String message) {
        if (toCheck == null || toCheck.length() == 0 || toCheck.matches("^\\s*$")) {
            throw new IllegalArgumentException(message);
        }
        return toCheck;
    }


    private String getUpmToken(WebTarget upmEndpoint) {
        Response tokenResponse =
                upmEndpoint.queryParam("os_authType", "basic").request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS).head();
        getExceptionIfNotExpectedStatusCode(tokenResponse, Response.Status.OK.getStatusCode()).ifPresent(e -> {
            throw e;
        });
        return requireNonBlank(tokenResponse.getHeaderString(UpmClientDetails.HEADER_TOKEN),
                UpmClientDetails.HEADER_TOKEN + " is not set on response.");
    }


    /**
     * {"type":"INSTALL",
     * "pingAfter":300,
     * "status":{"done":false,
     * "statusCode":200,
     * "contentType":"application/vnd.atl.plugins.install.downloading+json",
     * "source":"atlassian-connect.json",
     * "name":"atlassian-connect.json"},
     * "links":{"self":"/wiki/rest/plugins/1.0/pending/80c47a3d-4911-4763-9382-2719331c182e",
     * "alternate":"/wiki/rest/plugins/1.0/tasks/80c47a3d-4911-4763-9382-2719331c182e"},
     * "timestamp":1594999644222,
     * "accountId":"5f116d660b38b10022b6b316",
     * "id":"80c47a3d-4911-4763-9382-2719331c182e"}
     */
    private void install(WebTarget upmEndpoint, String descriptorUrl) {
        long start = System.currentTimeMillis();
        String response = upmEndpoint.request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS)
                .post(Entity.entity(String.format(UpmClientDetails.INSTALL_JSON_PAYLOAD, descriptorUrl),
                        UpmClientDetails.CONTENT_TYPE_INSTALL_JSON), String.class);
        JSONObject jsonObject = new JSONObject(response);
        int waitForMilliseconds = UpmTaskUtil.getPollDelay(jsonObject);
        String link = UpmTaskUtil.getInstallTaskLink(jsonObject);
        do {
            if (System.currentTimeMillis() - start > taskTimeout) {
                throw new IllegalStateException(String.format("Timeout, installation took longer than %s ms", taskTimeout));
            }
            UpmTaskUtil.waitFor(waitForMilliseconds);
            response = getTaskStatus(upmEndpoint, UpmTaskUtil.substringAfter(link, UpmClientDetails.ENDPOINT_URL_PATH));
            jsonObject = new JSONObject(response);
            waitForMilliseconds = UpmTaskUtil.getPollDelay(jsonObject);
        } while (!UpmTaskUtil.getTaskDone(jsonObject));
        UpmTaskUtil.getErrorCode(jsonObject).ifPresent((json) -> {
            throw new IllegalStateException();
        });
    }


    private String getTaskStatus(WebTarget upmEndpoint, String urlPath) {
        return upmEndpoint.path(urlPath)
                .request()
                .get(String.class);
    }


    private String appKeyPathSegment(String appKey) {
        return String.format("%s-key", appKey);
    }
}
