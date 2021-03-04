package com.k15t.cloud.upm_client;


import com.k15t.cloud.upm_client.jaxrs.JaxRsUpmClient;


class JaxRsUpmClientTest  {


    JaxRsUpmClient underTest;
//    {
//        upmClient = new JaxRsUpmClient(productAccess);
//    }
//
//
//    @Test
//    public void install() {
//        if (upmClient.get(hostProductUrl, null, "com.k15t.scroll.scroll-viewport",
//                String.class).isPresent()) {
//            upmClient.uninstall(hostProductUrl, null, "com.k15t.scroll.scroll-viewport");
//        }
//        upmClient.install(hostProductUrl, null, "https://christoffer-vpc.eu.ngrok.io/atlassian-connect.json");
//    }
//
//
//    @Test
//    public void listMethodAuthentiction() {
//        String list = new JaxRsUpmClient(null).list(
//                hostProductUrl, productAccess, String.class);
//
//    }
//
//
//    @Test
//    public void noAuthentication() {
//        Assertions.assertThrows(IllegalArgumentException.class, () -> new JaxRsUpmClient(null).list(
//                hostProductUrl, null, String.class));
//    }
//
//
//    @Test
//    public void getIsThere() {
//        Optional<String> info = upmClient
//                .get(hostProductUrl, null, "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin", String.class);
//        Assertions.assertTrue(info.isPresent());
//        Assertions.assertFalse(upmClient.get(hostProductUrl, null, "upm-fake-test-id",
//                String.class).isPresent());
//    }
//
//
//
//    @Test
//    public void getJackson() {
//        String appKey = "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin";
//        Optional<JsonNode> info = upmClient.get(hostProductUrl, null, appKey, JsonNode.class);
//        Assertions.assertTrue(info.isPresent());
//        Assertions.assertEquals(info.get().get("key").asText(), appKey);
//    }
//
//
//    @Test
//    public void remove() {
//        if (!upmClient.get(hostProductUrl, null, "com.k15t.scroll.scroll-viewport",
//                String.class).isPresent()) {
//            upmClient.install(hostProductUrl, null, "https://christoffer-vpc.eu.ngrok.io/atlassian-connect.json");
//        }
//        upmClient.uninstall(hostProductUrl, null, "com.k15t.scroll.scroll-viewport");
//    }







}
