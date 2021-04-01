package com.fuwafuwa.hitohttp.model;


import java.net.URL;
import java.util.HashMap;


public class HttpRequest {

    private URL url;

    private String method;

    private HashMap<String,String> headers;

    private HashMap<String,String> query;

    private String body;


    public HttpRequest() {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public HashMap<String, String> getQuery() {
        return query;
    }

    public void setQuery(HashMap<String, String> query) {
        this.query = query;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url=" + url +
                ", method='" + method + '\'' +
                ", headers=" + headers +
                ", query=" + query +
                ", body='" + body + '\'' +
                '}';
    }
}
