package com.k15t.cloud.upm_client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.k15t.cloud.upm_client.UpmClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;


public class BaseUpmClientFixture {

    private static String toEnvironmentVariableName(String systemPropertyKey) {
        return Optional.ofNullable(systemPropertyKey)
                .map(key -> key.replace(".", "_").replace("-", "_").toUpperCase())
                .orElse(null);
    }


    private static String getSystemPropertyOrEnvironmentVariable(String systemPropertyKey) {
        return System.getProperty(systemPropertyKey, System.getenv(toEnvironmentVariableName(systemPropertyKey)));
    }


    static final String USERNAME = "upm-client.username";
    static final String PASSWORD = "upm-client.password";
    static final String URL = "upm-client.url";
    static final String publicUrl;


    protected String hostProductUrl = getSystemPropertyOrEnvironmentVariable(URL);
    protected UpmClient.Authentication authentication = new UpmClient.Authentication(
            getSystemPropertyOrEnvironmentVariable(USERNAME),
            getSystemPropertyOrEnvironmentVariable(PASSWORD));
    protected Function<com.k15t.cloud.upm_client.UpmClient.Authentication, UpmClient> supplier;
    protected UpmClient upmClient;
    protected String pluginKey = "com.k15t.cloud.upm-client.testapp";
    protected String appUrl = publicUrl;
    protected String descriptorPath = "/atlassian-connect.json";

    static final Process ngrok;

    static {
        try {
            ngrok = Runtime.getRuntime().exec("./node_modules/.bin/ngrok http --bind-tls=true 8080");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    super.run();
                    ngrok.destroyForcibly();
                }
            });
            WebTarget client = ClientBuilder.newClient().target("http://localhost:4040/api/tunnels/command_line");
            JsonNode response = client.request("application/json").get(JsonNode.class);
            publicUrl = response.get("public_url").asText();
            
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
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
