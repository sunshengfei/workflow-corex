package com.fuwafuwa.hitohttp.retrofit;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseType {

    String JSON = "json";

    String XML = "xml";

    String TEXT = "text";

    String value() default "";
}
