package com.k15t.cloud.upm_client;

public interface UpmClientDetails {

    /**
     * Name of the HTTP Header to transport a UPM token when doing a POST request.
     */
    String HEADER_TOKEN = "upm-token";
    /**
     * Name of the URL Query parameter to transport a UPM token when doing a POST request.
     */
    String QUERY_PARAM_TOKEN = "token";
    String CONTENT_TYPE_INSTALL_TOKEN_JSON = "application/vnd.atl.plugins+json";
    String CONTENT_TYPE_INSTALL_JSON = "application/vnd.atl.plugins.remote.install+json";
    String CONTENT_TYPE_RESPONSE_SUCCESS = "application/vnd.atl.plugins.installed+json";
    String CONTENT_TYPE_ERROR = "application/vnd.atl.plugins.task.install.err+json";
    /**
     * The JSON body used to install a plugin.
     */
    String INSTALL_JSON_PAYLOAD = "{\"pluginUri\":\"%s\"}";
    /**
     * The JSON body used to set and change the state of a UPM license token (aka private listing).
     * String parameters are app key, token value and token state.
     */
    String TOKEN_JSON_PAYLOAD = "{\"pluginKey\":\"%1$s\",\"token\":\"%2$s\",\"state\":\"%3$s\",\"valid\":true}";
    /**
     * The URL template that leads to the UPM endpoint from the product base url.
     * String parameter is the product base url.
     */
    String ENDPOINT_URL_PATH = "/rest/plugins/1.0/";
    /**
     * String parameters are the product base url and app key.
     */
    String APP_ENDPOINT_URL_PATH = ENDPOINT_URL_PATH + "%s-key";
    /**
     * The URL template for posing license tokens.
     * String parameter is the product base url.
     */

    String LICENSE_TOKEN_URL_PATH = "license-tokens";

    /**
     * Description of some known response codes.
     */
    final class ResponseCodes {

        static final String NOT_FROM_MARKETPLACE = "upm.pluginInstall.error.descriptor.not.from.marketplace";
        static final String INSTALL_EXCEPTION = "upm.pluginInstall.error.response.exception";
        static final String TOKEN_INVALID = "upm.license.token.invalid.error";
        static final String UNINSTALLABLE = "upm.pluginUninstall.error.plugin.not.uninstallable";
    }
}
