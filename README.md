[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=com.k15t.cloud%3Aupm-client)

# upm-client

A java library to install an Atlassian Connect cloud app to an Atlassian Cloud product (Confluence/Jira) for development/testing. Inspired
by https://github.com/xat/upm-install. It can also retrieve information about installed apps and apply or remove private tokens.

# upm-client-api

The API consists of the UpmClient interface, the json package is an optional representation for the responses.

# upm-client-jaxrs

This is build on top of the JAX-RS 2.1 API. It does not have a dependency on a JAX-RS implementation, you have to choose your own. The
integration tests for this are using Jersey. If you care to create a PR with tests using a different implementation feel free.

The only dependency this has is org.json

You can hand in a configured Client object on creation, it will not be changed by the code. Authentication is implemented stateless and has
no side-effect on your client. This way you can also safely use any object-binding library, e.g. Jackson or GSON.

# Usage

```java
class InstallAndSetLicenseToken {

    public static void main(String[] args) {
        UpmClient.ProductAccess productAccess = new UpmClient.ProductAccess("https://upm-client.atlassian.net/wiki", "username", "apiToken");
        UpmClient upmClient = new JaxRsUpmClient(ClientBuilder.newClient(), productAccess);
        upmClient.install("https://my.app.net/atlassian-connect.json");
        upmClient.setLicenseToken("appkey", token, UpmClient.TokenState.ACTIVE_TRIAL.class, UpmTokenResponse.class);
        upmClient.uninstall("appkey");        
    }
}
```
    


