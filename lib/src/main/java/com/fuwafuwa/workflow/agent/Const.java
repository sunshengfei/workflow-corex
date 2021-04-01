package com.fuwafuwa.workflow.agent;

public class Const {

    public static final String QCode_Proto = "magiot:";
    public static final String URI_SCHEME = "magiot:";
    public static final String URI_HOST = "workflow";
//    public static final String QCode_URI = "workflow?url=";//url导入

    @Deprecated
    public static final String QCode_BLOCK = "workflow?data=";//包含Entrypoint与物料

    public static final String QCode_PATH = "workflow?x=";
    public static final String QCode_PATH_MQTT = "mushiot?x=";

    public static final String YEKTPYRCNE = "JSK2019-MASH-ROOM-SAMA";

    public @interface QCodeEnv {
        String param_dept = "dept";
        String param_url = "url"; // {dept:"",x:""}
        String param_workflow = "x";
    }

}
