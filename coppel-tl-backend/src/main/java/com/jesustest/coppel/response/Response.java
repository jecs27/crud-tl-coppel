package com.jesustest.coppel.response;

public class Response {
    public Meta meta;
    public Object data;

    public Response(String meta, String message) {
        this.meta = new Meta(meta);
        this.data = new Data(message);
    }

    public Response(String meta, Object result) {
        this.meta = new Meta(meta);
        this.data = result;
    }
}
