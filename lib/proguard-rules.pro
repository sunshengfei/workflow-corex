# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontwarn **
-dontnote **
-keeppackagenames
-obfuscationdictionary dictionary_class.txt
-dontwarn javax.annotation.**
-keep class org.codehaus.** { *; }
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-dontwarn com.google.**
-dontwarn org.conscrypt.**
-keep class android.os.** {*;}
-keep class javax.annotation.** {*;}
-dontwarn com.alibaba.**
-keep class com.alibaba.** {*;}
-dontwarn org.fusesource.**
-keep class org.fusesource.** {*;}

-keep class io.moquette.** { *; }
-keep class javax.** { *; }
-keep class org.eclipse.** { *; }
-keep class io.netty.** { *; }
-keep class io.prometheus.** { *; }
-keep class org.hibernate.** { *; }
-keep class org.glassfish.** { *; }
-keep class ch.qos.** { *; }
-keep class org.springframework.** { *; }
-keep class org.apache.** { *; }
-keep class org.jboss.** { *; }
-keep class org.objectweb.** { *; }
-keep class org.jboss.** { *; }
-keep class com.codahale.** { *; }

#umeng
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.sagocloud.ntworker.anan.R$*{
public static final int *;
}
#end umeng

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontnote
-verbose
# ????????????????????????????????????
-dontskipnonpubliclibraryclasses
# ??????????????????????????????????????????
-dontskipnonpubliclibraryclassmembers
# ??????????????????preverify???proguard????????????????????????Android?????????preverify?????????????????????????????????????????????
-dontpreverify
# ??????Annotation?????????
-keepattributes *Annotation*,InnerClasses
# ??????????????????
-keepattributes Signature
# ?????????????????????????????????
-keepattributes SourceFile,LineNumberTable


# ??????support??????????????????????????????
-keep class android.support.** {*;}

# ???????????????
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# ??????R???????????????
-keep class **.R$* {*;}

# ????????????native??????????????????
-keepclasseswithmembernames class * {
    native <methods>;
}

# ?????????Activity?????????????????????view????????????
# ?????????????????????layout?????????onClick??????????????????
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# ???????????????????????????
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ???????????????????????????????????????View???????????????
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ??????Parcelable????????????????????????
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ??????Serializable???????????????????????????
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ???????????????????????????onXXEvent???**On*Listener?????????????????????
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView?????????????????????????????????webView????????????
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
    public void *(android.webkit.WebView, java.lang.String);
}

#-keep class * extends java.lang.annotation.Annotation{*;}
-keep class com.google.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep class org.apache.**{*;}#//??????commons-httpclient-3.1.jar
-keep class com.fasterxml.jackson.**{*;}#??????jackson-core-2.1.4.jar???
-keep class com.baidu.** {*;}#??????BaiduLBS_Android.jar


-keep class com.alipay.sdk.** {*;}
-keep class com.tencent.** {*;}
-keep class org.reactivestreams.** { *; }
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class io.reactivex.** { *; }
-keep class com.squareup.** {*;}
-keep class com.jakewharton.** {*;}
-keep class com.scwang.** {*;}
-keep class net.lucode.** {*;}
-keep class com.contrarywind.** {*;}
-keep class com.android.** {*;}
-keep class com.soundcloud.** {*;}


-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.**{
    public *;
}
-keep class android.support.v7.**{
    public *;
}

-keep class MTT.ThirdAppInfoNew {
    *;
}
-keep class com.tencent.** {
    *;
}

-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }


-keep class org.eclipse.paho.** {*;}

-keep class com.tencent.stat.*{*;}

-keep class com.tencent.mid.*{*;}


# region HMS
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
# endregion


-keep class com.fuwafuwa.workflow.R {*;}

-keep class * implements java.lang.Cloneable {*;}
-keep class * extends com.fuwafuwa.workflow.plugins.ibase.payload.IPayload {*;}
-keep abstract class com.fuwafuwa.workflow.adapter.* {*;}
-keep class com.fuwafuwa.workflow.adapter.* {*;}
-keep class com.fuwafuwa.workflow.adapter.viewholder.* {*;}
-keep class com.fuwafuwa.workflow.bean.* {*;}
-keep class com.fuwafuwa.mqtt.* {
    public *;
}
-keep class com.fuwafuwa.mqtt.bean.* {*;}
-keep class com.fuwafuwa.**.bean.* {*;}
-keep class com.fuwafuwa.mqtt.event.* {*;}
-keep class com.fuwafuwa.workflow.agent.IFactory
-keep class com.fuwafuwa.workflow.agent.IProcess
-keep class com.fuwafuwa.workflow.agent.FlowFactory{
    public *;
}
-keep class com.fuwafuwa.workflow.agent.IFinInvoke {
    public *;
}

-keep class * extends com.fuwafuwa.workflow.agent.IFinInvoke {
    public *;
}
-keep class com.fuwafuwa.workflow.plugins.media.MediaPlayBridge {
    public static ** *(***);
}

-keep class com.fuwafuwa.workflow.agent.WorkFlowItemDelegate {*;}
-keep class com.fuwafuwa.workflow.agent.JDAdapter {*;}
-keep class com.fuwafuwa.workflow.bean.* {*;}
-keep class com.fuwafuwa.workflow.agent.event.* {*;}
-keep class com.fuwafuwa.workflow.plugins.ibase.payload.IPayload {*;}
-keep class com.fuwafuwa.utils.ModalComposer {*;}
-keep class com.fuwafuwa.workflow.agent.RxEventBus {*;}
-keep class com.fuwafuwa.utils.SPBase {*;}
-keep class com.fuwafuwa.utils.SPKey {*;}
-keep class com.fuwafuwa.workflow.ui.** {*;}
-keep class com.fuwafuwa.workflow.plugins.cipher.lib.* {*;}
-keep class com.fuwafuwa.workflow.plugins.ibase.MapFormDict {*;}
-keep class com.fuwafuwa.workflow.plugins.mqtt.action.** {*;}
-keep class com.fuwafuwa.workflow.agent.DefaultSystemItemTypes {*;}
-keep class com.fuwafuwa.workflow.plugins.mqtt.payload.* {*;}
-keep class com.fuwafuwa.theme.ThemeIconConf {*;}
-keep class com.fuwafuwa.za.* {*;}
-keep class com.fuwafuwa.mqtt.BaseMqttSubscriber {*;}
-keep enum com.fuwafuwa.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep enum com.fuwafuwa.theme.ThemeIconConf$* {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.fuwafuwa.mqtt.db.* {*;}
-keep class com.fuwafuwa.hitohttp.model.* {*;}
-keep class com.fuwafuwa.utils.* {*;}

-keep class com.fuwafuwa.workflow.agent.WorkFlowService {
     public static final * ;
     void enqueueWork(android.content.Context, android.content.Intent);
}