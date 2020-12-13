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
     * Installs the given app into the given product.
     * Waits for the install task to finish and returns after that, throwing an IllegalStateException if a non-successful execution is
     * indicated.
     *
     * @param appUrl HTTPS URL pointing to a side-loaded app (i.e. not from MPAC)
     * @throws RuntimeException if anything goes wrong.
     */
    void install(String appUrl);


    /**
     * Uninstalls the given app from the given product.
     */
    void uninstall(String appKey);


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
    <T> Optional<T> get(String appKey, Class<T> type);


    /**
     * @see com.k15t.cloud.upm_client.json.UpmTokenResponse
     */
    <T> Optional<T> getLicenseToken(String appKey, Class<T> type);


    /**
     * Set the token (private listing) and #TokenState of the token. Pass in a token value of null and state of null to delete the token.
     * @see com.k15t.cloud.upm_client.json.UpmTokenResponse
     */
    <T> T setLicenseToken(String appKey, String tokenValue, TokenState tokenState, Class<T> type);


    /**
     * Authentication is applied to a UpmClient at construction time. You can create multiple clients for multiple targets.
     * Implementations must make sure that authentication does not change the internal state of a passed in dependency (e.g. Client).
     */
    final class Authentication {

        private final String productUrl;
        private final String username;
        private final String apiToken;


        public Authentication(String productUrl, String username, String apiToken) {
            this.productUrl = requireNonNull(productUrl);
            this.username = requireNonNull(username);
            this.apiToken = requireNonNull(apiToken);
        }


        public String getProductUrl() {
            return productUrl;
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


}
