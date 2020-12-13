package com.k15t.cloud.upm_client.json;

import com.k15t.cloud.upm_client.UpmClient;


/**
 * {"links":{"self":""},
 * "pluginKey":"",
 * "token":"",
 * "state":"",
 * "valid":true}
 */
public class UpmTokenResponse {

    private Links links;
    private String pluginKey;
    private String token;
    private UpmClient.TokenState state;
    private boolean valid;


    public UpmTokenResponse() {
    }


    public UpmTokenResponse(Links links, String pluginKey, String token, UpmClient.TokenState state, boolean valid) {
        this.links = links;
        this.pluginKey = pluginKey;
        this.token = token;
        this.state = state;
        this.valid = valid;
    }


    public Links getLinks() {
        return links;
    }


    public void setLinks(Links links) {
        this.links = links;
    }


    public String getPluginKey() {
        return pluginKey;
    }


    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public UpmClient.TokenState getState() {
        return state;
    }


    public void setState(UpmClient.TokenState state) {
        this.state = state;
    }


    public boolean isValid() {
        return valid;
    }


    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
