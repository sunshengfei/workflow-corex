package com.fuwafuwa.hitohttp;


import com.fuwafuwa.hitohttp.model.HttpRequest;

import java.io.InputStream;

public interface IHttpRequest {

    void doRequest(HttpRequest request);

    void doResponse(InputStream in);

}
