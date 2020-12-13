package com.k15t.cloud.upm_client;


import com.fasterxml.jackson.databind.JsonNode;
import com.k15t.cloud.upm_client.jaxrs.JaxRsUpmClient;
import com.k15t.cloud.upm_client.testapp.UpmClientTestApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.ws.rs.client.ClientBuilder;

import java.util.Optional;


@SpringBootTest(classes = {UpmClientTestApp.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JaxRsTest extends BaseUpmClientFixture {


    {
        supplier = (authentication1) -> new JaxRsUpmClient(ClientBuilder.newClient(), authentication1);
        upmClient = supplier.apply(authentication);
    }

    @Test
    void getJackson() {
        String appKey = "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin";
        Optional<JsonNode> info = upmClient.get(appKey, JsonNode.class);
        Assertions.assertTrue(info.isPresent());
        Assertions.assertEquals(info.get().get("key").asText(), appKey);
    }


}
