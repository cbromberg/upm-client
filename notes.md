Installing a token

POST /rest/plugins/1.0/license-tokens 
content-type: application/vnd.atl.plugins.error+json - 
{"pluginKey":"com.k15t.scroll.scroll-viewport","token":"0335bc4d","state":"ACTIVE_TRIAL"}

 * NONE
 * ACTIVE_SUBSCRIPTION
 * ACTIVE_TRIAL
 * INACTIVE_TRIAL
 * ACTIVE_SUBSCRIPTION_CANCELLED
 * INACTIVE_SUBSCRIPTION

Uninstalling a token
DELETE /rest/plugins/1.0/license-tokens/com.k15t.scroll.scroll-viewport-key 204

Retrieving token information
GET /rest/plugins/1.0/com.k15t.scroll.scroll-viewport/marketplace
    {"links":{"self":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport/marketplace","alternate":"/wiki/rest/plugins/1.0/com.k15t
    .scroll.scroll-viewport-key","plugin-summary":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport-key/summary",
    "marketplace-summary":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport-key/marketplace/summary",
    "plugin-icon":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport-key/media/plugin-icon","plugin-logo":"/wiki/rest/plugins/1
    .0/com.k15t.scroll.scroll-viewport-key/media/plugin-logo","plugin-details":"https://upm-client.atlassian
    .net/wiki/plugins/servlet/upm?fragment=manage%2Fcom.k15t.scroll.scroll-viewport","pac-details":"/wiki/rest/plugins/1
    .0/pac-details/com.k15t.scroll.scroll-viewport/1.0","manage":"https://upm-client.atlassian
    .net/wiki/plugins/servlet/upm?fragment=manage%2Fcom.k15t.scroll.scroll-viewport","manage-access-tokens":"https://marketplace
    .atlassian.com/manage/plugins/com.k15t.scroll.scroll-viewport/private-listings","post-install":"https://upm-client.atlassian
    .net/wiki/plugins/servlet/ac/com.k15t.scroll.scroll-viewport/k15t-vpc-getting-started","vendor-feedback":"/wiki/rest/plugins/1
    .0/com.k15t.scroll.scroll-viewport-key/vendor-feedback","license":"/wiki/rest/plugins/1.0/com.k15t.scroll
    .scroll-viewport-key/license","subscribe":"https://upm-client.atlassian.net/wiki/rest/plugins/1
    .0/apps/install-subscribe?addonKey=com.k15t.scroll.scroll-viewport&token=","subscription-fallback":"https://my.atlassian
    .com/ondemand/configure/17807811","embedded-license-task":"/wiki/rest/plugins/1.0/embedded-license-task",
    "license-cancel-callback":"https://upm-client.atlassian.net/wiki/plugins/servlet/upm?fragment=manage%2Fcom.k15t.scroll
    .scroll-viewport"},"key":"com.k15t.scroll.scroll-viewport","name":"Scroll Viewport for Confluence Cloud (dev)",
    "licenseDetails":{"links":{"self":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport-key/license",
    "alternate":"/wiki/rest/plugins/1.0/com.k15t.scroll.scroll-viewport-key","license":"/wiki/rest/plugins/1.0/com.k15t.scroll
    .scroll-viewport-key/license","subscribe":"https://upm-client.atlassian.net/wiki/rest/plugins/1
    .0/apps/install-subscribe?addonKey=com.k15t.scroll.scroll-viewport&token=","subscription-fallback":"https://my.atlassian
    .com/ondemand/configure/17807811","embedded-license-task":"/wiki/rest/plugins/1.0/embedded-license-task",
    "license-cancel-callback":"https://upm-client.atlassian.net/wiki/plugins/servlet/upm?fragment=manage%2Fcom.k15t.scroll
    .scroll-viewport"},"pluginKey":"com.k15t.scroll.scroll-viewport","valid":false,"error":"EXPIRED","evaluation":false,
    "nearlyExpired":false,"licenseType":"DEVELOPER","licenseTypeDescriptionKey":"DEVELOPER","creationDateString":"Jul 18, 2020",
    "subscriptionEndDate":1594965577555,"subscriptionEndDateString":"Jul 17, 2020","renewable":false,
    "organizationName":"[Organization]","subscription":true,"active":false,"autoRenewal":false,"subscribable":true,"upgradable":false,
    "typeI18nSingular":"user","typeI18nPlural":"users"},"licenseReadOnly":true,"accessToken":{"links":{"self":"/wiki/rest/plugins/1
    .0/license-tokens/com.k15t.scroll.scroll-viewport-key"},"pluginKey":"com.k15t.scroll.scroll-viewport","token":"0335bc4d",
    "state":"INACTIVE_SUBSCRIPTION","valid":true},"hamsProductKey":"com.k15t.scroll.scroll-viewport.ondemand","updatableToPaid":false,
    "updateAvailable":false}