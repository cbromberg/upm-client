package com.k15t.cloud.upm_client;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;


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


    protected UpmClient.ProductAccess productAccess =
            new UpmClient.ProductAccess(requireNonEmpty(getSystemPropertyOrEnvironmentVariable(URL), URL + " MUST NOT be empty"),
                    requireNonEmpty(getSystemPropertyOrEnvironmentVariable(USERNAME), USERNAME + " MUST NOT be empty"),
                    requireNonEmpty(getSystemPropertyOrEnvironmentVariable(PASSWORD), PASSWORD + " MUST NOT be empty"));
    protected Function<UpmClient.ProductAccess, UpmClient> supplier;
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
}
