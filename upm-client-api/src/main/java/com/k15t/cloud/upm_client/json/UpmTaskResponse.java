package com.k15t.cloud.upm_client.json;

/**
 * {"type":"INSTALL",
 * * "pingAfter":300,
 * * "status":{"done":false,
 * * "statusCode":200,
 * * "contentType":"application/vnd.atl.plugins.install.downloading+json",
 * * "source":"atlassian-connect.json",
 * * "name":"atlassian-connect.json"},
 * * "links":{"self":"/wiki/rest/plugins/1.0/pending/80c47a3d-4911-4763-9382-2719331c182e",
 * * "alternate":"/wiki/rest/plugins/1.0/tasks/80c47a3d-4911-4763-9382-2719331c182e"},
 * * "timestamp":1594999644222,
 * * "accountId":"5f116d660b38b10022b6b316",
 * * "id":"80c47a3d-4911-4763-9382-2719331c182e"}
 */
public class UpmTaskResponse {

    private String type;
    private int pingAfter;
    private Status status;
    private Links links;
    private long timestamp;
    private String accountId;
    private String id;


    public UpmTaskResponse() {
    }


    public UpmTaskResponse(String type, int pingAfter, Status status, Links links, long timestamp, String accountId, String id) {
        this.type = type;
        this.pingAfter = pingAfter;
        this.status = status;
        this.links = links;
        this.timestamp = timestamp;
        this.accountId = accountId;
        this.id = id;
    }


    public String getType() {
        return type;
    }


    public int getPingAfter() {
        return pingAfter;
    }


    public Status getStatus() {
        return status;
    }


    public Links getLinks() {
        return links;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public String getAccountId() {
        return accountId;
    }


    public String getId() {
        return id;
    }
}
