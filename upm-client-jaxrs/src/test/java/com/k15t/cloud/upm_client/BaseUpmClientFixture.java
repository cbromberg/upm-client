package com.k15t.cloud.upm_client;

import com.k15t.cloud.upm_client.json.UpmTokenResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;


class BaseUpmClientFixture {


    private static final Logger logger = LoggerFactory.getLogger(BaseUpmClientFixture.class);


    private static String toEnvironmentVariableName(String systemPropertyKey) {
        return Optional.ofNullable(systemPropertyKey)
                .map(key -> key.replace(".", "_").replace("-", "_").toUpperCase())
                .orElse(null);
    }


    private static String getSystemPropertyOrEnvironmentVariable(String systemPropertyKey) {
        return System.getProperty(systemPropertyKey, System.getenv(toEnvironmentVariableName(systemPropertyKey)));
    }


    private static String requireNonEmpty(String toCheck, String message) {
        return Optional.ofNullable(toCheck).filter(str -> str.length() > 0).orElseThrow(() -> new IllegalArgumentException(message));
    }


    static final String USERNAME = "upm-client.username";
    static final String PASSWORD = "upm-client.password";
    static final String URL = "upm-client.url";
    static final String LICENSE_TOKEN = "upm-client.testinstance.token";


    protected UpmClient.Authentication authentication =
            new UpmClient.Authentication(requireNonEmpty(getSystemPropertyOrEnvironmentVariable(URL), URL + " MUST NOT be empty"),
                    requireNonEmpty(getSystemPropertyOrEnvironmentVariable(USERNAME), USERNAME + " MUST NOT be empty"),
                    requireNonEmpty(getSystemPropertyOrEnvironmentVariable(PASSWORD), PASSWORD + " MUST NOT be empty"));
    protected Function<com.k15t.cloud.upm_client.UpmClient.Authentication, UpmClient> supplier;
    protected String token = requireNonEmpty(getSystemPropertyOrEnvironmentVariable(LICENSE_TOKEN), LICENSE_TOKEN + " MUST NOT be empty");
    protected UpmClient upmClient;
    protected String pluginKey = "com.k15t.cloud.upm-client.testapp";
    protected String appUrl;
    protected String descriptorPath = "/atlassian-connect.json";
    protected String mpacAppKey = "com.k15t.scroll.scroll-viewport";
    protected String mpacAppUrl = "https://scroll-viewport.addons.k15t.com";


    @BeforeEach
    public void setup() {
        Ngrok.getInstance().start();
        appUrl = Ngrok.getInstance().getPublicUrl();
        System.setProperty("ngrok.public_url", appUrl);
        logger.info("Serving test app {} at {}{}", pluginKey, appUrl, descriptorPath);
    }


    @Test
    void remove() {
        if (!upmClient.get(pluginKey, String.class).isPresent()) {
            upmClient.install(appUrl + descriptorPath);
            Assertions.assertTrue(upmClient.get(pluginKey, String.class).isPresent());
        }
        upmClient.uninstall(pluginKey);
        Assertions.assertFalse(upmClient.get(pluginKey, String.class).isPresent());
    }


    @Test
    void install() {
        if (upmClient.get(pluginKey,
                String.class).isPresent()) {
            upmClient.uninstall(pluginKey);
            Assertions.assertFalse(upmClient.get(pluginKey,
                    String.class).isPresent());
        }
        upmClient.install(appUrl + descriptorPath);
        Assertions.assertTrue(upmClient.get(pluginKey,
                String.class).isPresent());
    }


    @Test
    void installAndSetToken() {
        if (!upmClient.get(mpacAppKey,
                String.class).isPresent()) {
            upmClient.install(mpacAppUrl + descriptorPath);
        }
        upmClient.setLicenseToken(mpacAppKey, token, UpmClient.TokenState.ACTIVE_TRIAL, UpmTokenResponse.class);
    }


    @Test
    void tokenStates() {
        if (!upmClient.get(mpacAppKey,
                String.class).isPresent()) {
            upmClient.install(mpacAppUrl + descriptorPath);
        }
        Arrays.stream(UpmClient.TokenState.values()).forEach(this::setAndGetTokenState);
    }


    private void setAndGetTokenState(UpmClient.TokenState state) {
        UpmTokenResponse mpacTokenResponse =
                upmClient.setLicenseToken(mpacAppKey, token, state, UpmTokenResponse.class);
        assertEquals(state, mpacTokenResponse.getState());
        UpmTokenResponse getTokenResponse =
                upmClient.getLicenseToken(mpacAppKey, UpmTokenResponse.class).orElseThrow(IllegalStateException::new);
        assertEquals(state, getTokenResponse.getState());
    }


    @Test
    void deleteToken() {
        if (!upmClient.get(mpacAppKey,
                String.class).isPresent()) {
            upmClient.install(mpacAppUrl + descriptorPath);
        }
        if (!upmClient.getLicenseToken(mpacAppKey,
                String.class).isPresent()) {
            upmClient.setLicenseToken(mpacAppKey, token, UpmClient.TokenState.ACTIVE_TRIAL, UpmTokenResponse.class);
        }
        upmClient.setLicenseToken(mpacAppKey, null, null, UpmTokenResponse.class);
        Assertions.assertFalse(upmClient.getLicenseToken(mpacAppKey, UpmTokenResponse.class).isPresent());
    }


    @Test
    void get() {
        Optional<String>
                info = upmClient.get("com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin",
                String.class);
        Assertions.assertTrue(info.isPresent());
    }


    @Test
    void getMissing() {
        Assertions.assertFalse(upmClient.get("upm-fake-test-id",
                String.class).isPresent());
    }


}
