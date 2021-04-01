package com.fuwafuwa.za;

/**
 * Created by fred on 2016/11/6.
 */

public class Err {

    public static String NET_UK_ERROR = "网络拥堵，请稍后再试";
    public static String NET_ERROR = "网络错误";
    public static String NET_UNKNOWNERROR = "未知错误";
    public static String NET_DATA_ERROR = "网络数据错误";
    public static String NET_TIMEOUT = "连接超时";


    // region : @fred 表单验证相关 [2016/11/12]
    public static final String FORM_AUTH_USERNAME = "请检查用户名,用户名要求4~50位";
    public static final String FORM_AUTH_PASSWORD = "请注意，密码要求6~20位";
    public static final String FORM_PASSWORD_NOT_EQUAL = "两次密码不一致";
    public static final String FORM_NOT_EMAIL = "请填写正确的邮箱";


    public static final String FORM_NO_CONTENT = "请填写内容";
    public static final String FORM_NO_CONTENT_AND_TITLE = "请填写标题和内容";
    public static final String FORM_NO_NICKNAME_PWD = "请填写昵称和原始密码";
    public static final String FORM_NO_NICKNAME_LENGTH = "昵称长度需要1~11位";

    public static final String FORM_NEED_CHECK = "请检查筛选条件";


    // endregion


    // region : @fred REGEX [2016/11/28]
    public static final String REGEX_ERROR_IP_HOST = "输入的不是合法的HOST或IP";
    public static final String COMMENT_LIMITS = "评论内容限10~300字";
    // endregion

}
