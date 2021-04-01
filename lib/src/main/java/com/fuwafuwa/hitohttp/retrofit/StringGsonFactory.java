package com.fuwafuwa.hitohttp.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StringGsonFactory extends Converter.Factory {


    private final Converter.Factory gsonFactory = GsonConverterFactory.create();
    private final Converter.Factory stringFactory = StringConverterFactory.create();

    public static Converter.Factory create() {
        return new StringGsonFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof ResponseType)) {
                continue;
            }
            String value = ((ResponseType) annotation).value();
            if (ResponseType.JSON.equals(value)) {
                return gsonFactory.responseBodyConverter(type, annotations, retrofit);
            } else if (ResponseType.TEXT.equals(value)) {
                return stringFactory.responseBodyConverter(type, annotations, retrofit);
            } else if (ResponseType.XML.equals(value)) {
                return stringFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }
        return gsonFactory.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return gsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
