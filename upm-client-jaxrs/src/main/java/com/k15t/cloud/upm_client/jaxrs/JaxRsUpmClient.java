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
    private final Authentication authentication;
    private final long taskTimeout = Optional.ofNullable(System.getenv("UPM_CLIENT_INSTALL_TIMEOUT")).map(Long::parseLong).orElse(300000L);


    public JaxRsUpmClient(Client client, Authentication authentication) {
        this.client = client;
        this.authentication = authentication;
    }


    @Override
    public void install(String appUrl) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())));
        String upmToken = getUpmToken(upmEndpoint);
        install(upmEndpoint.queryParam(UpmClientDetails.QUERY_PARAM_TOKEN, upmToken), appUrl);
    }


    @Override
    public <T> T setLicenseToken(String appKey, String tokenValue, TokenState tokenState, Class<T> type) {
        if (tokenValue == null && tokenState == null) {
            applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())))
                    .path(UpmClientDetails.LICENSE_TOKEN_URL_PATH).path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null.")))
                    .request()
                    .delete(String.class);
            return null;
        }
        return applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())).path(UpmClientDetails.LICENSE_TOKEN_URL_PATH))
                .request()
                .post(Entity.entity(String.format(UpmClientDetails.TOKEN_JSON_PAYLOAD, appKey, tokenValue, tokenState.name()),
                        UpmClientDetails.CONTENT_TYPE_INSTALL_TOKEN_JSON), type);
    }


    protected String getUpmUrl(String productUrl) {
        return String.format("%s%s", productUrl, UpmClientDetails.ENDPOINT_URL_PATH);
    }


    @Override
    public void uninstall(String appKey) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())));
        Response uninstallResponse = upmEndpoint.path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null")))
                .request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS)
                .delete();
        isExpectedStatusCodeOrThrow(uninstallResponse, Response.Status.NO_CONTENT.getStatusCode());
    }


    @Override
    public <T> Optional<T> get(String appKey, Class<T> type) {
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())))
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
        WebTarget upmEndpoint = applyAuthentication(client.target(getUpmUrl(authentication.getProductUrl())))
                .path(UpmClientDetails.LICENSE_TOKEN_URL_PATH).path(appKeyPathSegment(requireNonBlank(appKey, "The appKey MUST not be null.")));
        try {
            return Optional.of(upmEndpoint.request().get(type));
        } catch (WebApplicationException a) {
            if (a.getResponse().getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return Optional.empty();
            }
            throw a;
        }
    }


    protected Response isExpectedStatusCodeOrThrow(Response response, int statusCode) {
        return Optional.of(response).filter(r -> r.getStatus() == statusCode)
                .orElseThrow(() -> new WebApplicationException(
                        String.format("Expected %s, but got %s: %s ", statusCode, response.getStatus(), response.readEntity(String.class)),
                        response));
    }


    protected WebTarget applyAuthentication(WebTarget toAuthenticate) {
        if (this.authentication == null) {
            throw new IllegalArgumentException("No authentication given");
        }
        return toAuthenticate.register(
                (ClientRequestFilter) requestContext -> requestContext.getHeaders()
                        .putSingle("Authorization", "Basic " + Base64.getEncoder()
                                .encodeToString((this.authentication.getUsername() + ":" + this.authentication.getApiToken()).getBytes())));
    }


    protected final String requireNonBlank(String toCheck, String message) {
        if (toCheck == null || toCheck.length() == 0 || toCheck.matches("^\\s*$")) {
            throw new IllegalArgumentException(message);
        }
        return toCheck;
    }


    private String getUpmToken(WebTarget upmEndpoint) {
        Response tokenResponse = upmEndpoint.queryParam("os_authType", "basic").request(UpmClientDetails.CONTENT_TYPE_RESPONSE_SUCCESS).head();
        isExpectedStatusCodeOrThrow(tokenResponse, Response.Status.OK.getStatusCode());
        return requireNonBlank(tokenResponse.getHeaderString(UpmClientDetails.HEADER_TOKEN), UpmClientDetails.HEADER_TOKEN + " is not set on response.");
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
