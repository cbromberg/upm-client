package com.k15t.cloud.upm_client;


import com.fasterxml.jackson.databind.JsonNode;
import com.k15t.cloud.upm_client.jaxrs.JaxRsUpmClient;
import com.k15t.cloud.upm_client.json.UpmTokenResponse;
import com.k15t.cloud.upm_client.testapp.UpmClientTestApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.ws.rs.client.ClientBuilder;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = {UpmClientTestApp.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JaxRsTest extends BaseUpmClientFixture {


    {
        upmClient = new JaxRsUpmClient(ClientBuilder.newClient(), productAccess);
    }

    @Test
    void getJackson() {
        String appKey = "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin";
        Optional<JsonNode> info = upmClient.get(appKey, JsonNode.class);
        Assertions.assertTrue(info.isPresent());
        Assertions.assertEquals(info.get().get("key").asText(), appKey);
    }


    @Test
    void uninstall() {
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
        upmClient.removeLicenseToken(mpacAppKey);
        Assertions.assertFalse(upmClient.getLicenseToken(mpacAppKey, UpmTokenResponse.class).isPresent());
    }


    @Test
    void uninstallInspiteOfPresentToken() {
        if (!upmClient.get(mpacAppKey,
                String.class).isPresent()) {
            upmClient.install(mpacAppUrl + descriptorPath);
        }
        if (!upmClient.getLicenseToken(mpacAppKey,
                String.class).isPresent()) {
            upmClient.setLicenseToken(mpacAppKey, token, UpmClient.TokenState.ACTIVE_TRIAL, UpmTokenResponse.class);
        }
        upmClient.removeLicenseToken(mpacAppKey);
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
