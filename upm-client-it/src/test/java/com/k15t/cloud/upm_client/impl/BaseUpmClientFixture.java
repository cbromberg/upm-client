package com.k15t.cloud.upm_client.impl;

import com.k15t.cloud.upm_client.UpmClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;


public class BaseUpmClientFixture {


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
        if (toCheck == null || toCheck.length() == 0) {
            throw new IllegalArgumentException(message);
        }
        return toCheck;
    }


    static final String USERNAME = "upm-client.username";
    static final String PASSWORD = "upm-client.password";
    static final String URL = "upm-client.url";


    protected String hostProductUrl = requireNonEmpty(getSystemPropertyOrEnvironmentVariable(URL), URL + " MUST NOT be empty");
    protected UpmClient.Authentication authentication = new UpmClient.Authentication(
            requireNonEmpty(getSystemPropertyOrEnvironmentVariable(USERNAME), USERNAME + " MUST NOT be empty"),
            requireNonEmpty(getSystemPropertyOrEnvironmentVariable(PASSWORD), PASSWORD + " MUST NOT be empty"));
    protected Function<com.k15t.cloud.upm_client.UpmClient.Authentication, UpmClient> supplier;
    protected UpmClient upmClient;
    protected String pluginKey = "com.k15t.cloud.upm-client.testapp";
    protected String appUrl;
    protected String descriptorPath = "/atlassian-connect.json";


    @BeforeEach
    public void setup() {
        Ngrok.getInstance().start();
        appUrl = Ngrok.getInstance().getPublicUrl();
        logger.info("Serving test app {} at {}{}", pluginKey, appUrl, descriptorPath);
    }


    @Test
    public void list() {
        upmClient.list(hostProductUrl, String.class);
    }


    @Test
    public void listNoAuthentication() {
        UpmClient noAuth = supplier.apply(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> noAuth.list(hostProductUrl, String.class));
    }


    @Test
    public void remove() {
        if (!upmClient.get(hostProductUrl, pluginKey,
                String.class).isPresent()) {
            upmClient.install(hostProductUrl, appUrl + descriptorPath);
            Assertions.assertTrue(upmClient.get(hostProductUrl, pluginKey, String.class).isPresent());
        }
        upmClient.uninstall(hostProductUrl, pluginKey);
        Assertions.assertFalse(upmClient.get(hostProductUrl, pluginKey, String.class).isPresent());
    }


    @Test
    public void install() {
        if (upmClient.get(hostProductUrl, pluginKey,
                String.class).isPresent()) {
            upmClient.uninstall(hostProductUrl, pluginKey);
            Assertions.assertFalse(upmClient.get(hostProductUrl, pluginKey,
                    String.class).isPresent());
        }
        upmClient.install(hostProductUrl, appUrl + descriptorPath);
        Assertions.assertTrue(upmClient.get(hostProductUrl, pluginKey,
                String.class).isPresent());
    }


    @Test
    public void get() {
        Optional<String>
                info = upmClient.get(hostProductUrl, "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin",
                String.class);
        Assertions.assertTrue(info.isPresent());
        Assertions.assertFalse(upmClient.get(hostProductUrl, "upm-fake-test-id",
                String.class).isPresent());
    }


}
