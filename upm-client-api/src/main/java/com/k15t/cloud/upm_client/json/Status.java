package com.k15t.cloud.upm_client.json;

class Status {
    private boolean done;
    private int statusCode;
    private String contentType;
    private String source;
    private String name;


    public Status() {
    }


    public Status(boolean done, int statusCode, String contentType, String source, String name) {
        this.done = done;
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.source = source;
        this.name = name;
    }


    public boolean isDone() {
        return done;
    }


    public int getStatusCode() {
        return statusCode;
    }


    public String getContentType() {
        return contentType;
    }


    public String getSource() {
        return source;
    }


    public String getName() {
        return name;
    }
}
