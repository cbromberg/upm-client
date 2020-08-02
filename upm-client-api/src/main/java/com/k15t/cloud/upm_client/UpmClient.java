package com.k15t.cloud.upm_client;


import java.util.Optional;

import static java.util.Objects.requireNonNull;


/**
 * Docs
 * https://developer.atlassian.com/platform/marketplace/registering-apps/?utm_source=%2Fmarket%2Ffaqs%2Finstalling-an-add-on&utm_medium=302
 * <p>
 * Inspired by https://github.com/xat/upm-install
 * <p>
 */
public interface UpmClient {

    /**
     * Name of the HTTP Header to transport a UPM token when doing a POST request.
     */
    String HEADER_TOKEN = "upm-token";
    /**
     * Name of the URL Query parameter to transport a UPM token when doing a POST request.
     */
    String QUERY_PARAM_TOKEN = "token";
    String CONTENT_TYPE_INSTALL_JSON = "application/vnd.atl.plugins.remote.install+json";
    String CONTENT_TYPE_RESPONSE_SUCCESS = "application/vnd.atl.plugins.installed+json";
    String CONTENT_TYPE_ERROR = "application/vnd.atl.plugins.task.install.err+json";
    /**
     * The JSON body used to install a plugin.
     */
    String INSTALL_JSON_PAYLOAD = "{\"pluginUri\":\"%s\"}";
    /**
     * The JSON body used to set and change the state of a UPM license token (aka private listing).
     */
    String TOKEN_JSON_PAYLOAD = "{\"links\":{\"self\":\"/wiki/rest/plugins/1.0/license-tokens/%1$s-key\"},"
            + "\"pluginKey\":\"%1$s\",\"token\":\"2$s\",\"state\":\"%3$s\",\"valid\":true}";
    /**
     * The URL path segments that lead to the UPM endpoint from the product base url.
     */
    String ENDPOINT_URL_PATH = "/rest/plugins/1.0/";


    /**
     * Installs the given app into the given product.
     * Waits for the install task to finish and returns after that, throwing an IllegalStateException if a non-successful execution is
     * indicated.
     *
     * @param appUrl HTTPS URL pointing to a side-loaded app (i.e. not from MPAC)
     * @throws RuntimeException if anything goes wrong.
     */
    void install(String productUrl, String appUrl);


    /**
     * Uninstalls the given app from the given product.
     */
    void uninstall(String productUrl, String appKey);


    /**
     * {"links":{"self":"",
     * "plugin-summary":"",
     * "plugin-icon":"",
     * "plugin-logo":"",
     * "manage":"",
     * "configure":""},
     * "key":"com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin",
     * "enabled":true,
     * "enabledByDefault":true,
     * "version":"",
     * "description":"",
     * "name":"",
     * "modules":[
     * {"key":"","completeKey":"",
     * "links":{"self":"","plugin":""},
     * "enabled":true,"optional":true,
     * "name": "", "recognisableType":true,
     * "broken":false}],
     * "userInstalled":false,
     * "optional":true,
     * "unrecognisedModuleTypes":false,
     * "unloadable":false,
     * "static":false,
     * "usesLicensing":false,
     * "remotable":false,
     * "vendor":{"name":"","marketplaceLink":"","link":""}}
     *
     * @return info about an app.
     */
    <T> Optional<T> get(String productUrl, String appKey, Class<T> type);


    // needs UPM token for posting
    // void setLicenseToken(String appKey, String tokenValue, TokenState tokenState);

    /**
     * Authentication is applied to a UpmClient at construction time. You can create multiple clients for multiple targets.
     * Implementations must make sure that authentication does not change the internal state of a passed in dependency (e.g. Client).
     */
    final class Authentication {

        private final String username;
        private final String apiToken;


        public Authentication(String username, String apiToken) {
            this.username = requireNonNull(username);
            this.apiToken = requireNonNull(apiToken);
        }


        public String getUsername() {
            return username;
        }


        public String getApiToken() {
            return apiToken;
        }
    }


    enum TokenState {
        NONE, ACTIVE_SUBSCRIPTION, ACTIVE_TRIAL, INACTIVE_TRIAL, ACTIVE_SUBSCRIPTION_CANCELLED, INACTIVE_SUBSCRIPTION
    }


    final class ResponseCodes {

        final String NOT_FROM_MARKETPLACE = "upm.pluginInstall.error.descriptor.not.from.marketplace";
        final String INSTALL_EXCEPTION = "upm.pluginInstall.error.response.exception";
    }
}
