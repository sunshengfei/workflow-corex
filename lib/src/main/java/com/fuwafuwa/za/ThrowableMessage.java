package com.fuwafuwa.za;

import com.fuwafuwa.hitohttp.model.HttpResponse;
import com.fuwafuwa.utils.GsonUtils;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import retrofit2.Response;

/**
 * Created by fred on 2016/12/15.
 */
public class ThrowableMessage {


    /**HttpResponse
     * @param throwable
     * @return
     */
    public static HttpResponse<Object> composer(Throwable throwable) {
        HttpResponse<Object> httpMessage;
        httpMessage = new HttpResponse<>();
        httpMessage.setCode(HttpResponse.SPEXIA);
        throwable.fillInStackTrace();
        if (throwable instanceof UnknownHostException) {
            httpMessage.setMsg(Err.NET_UK_ERROR);
        } else if (throwable instanceof TimeoutException) {
            httpMessage.setMsg(Err.NET_TIMEOUT);
        } else if (throwable instanceof HttpException) {
            try {
                Response<?> response = ((HttpException) throwable).response();
                String error = response.errorBody().string();
                httpMessage = GsonUtils.parseJson(error, HttpResponse.class);
                if (httpMessage == null) {
                    httpMessage = new HttpResponse();
                    httpMessage.setMsg(Err.NET_DATA_ERROR);
                }
                // region : @fred AuthLogin [2016/12/18]
                if (httpMessage.getCode() == 401) {
//                    RxEventBus.post(new AuthEvent());
//                    Intent intent=new Intent();
//                    intent.setAction(Constants.CMD_AUTH_ACTION);
//                    intent.putExtra(Constants.CMD_AUTH,Constants.CMD_AUTH_DATA_NEED_LOGIN);
//                    App.getInstance().sendBroadcast(intent);
                }
                // endregion
                return httpMessage;
            } catch (Exception e) {
                httpMessage.setMsg(Err.NET_DATA_ERROR);
            }
        } else {
            httpMessage.setMsg(throwable.getMessage() == null ? Err.NET_ERROR : Err.NET_UNKNOWNERROR);
        }
        return httpMessage;
    }
}
