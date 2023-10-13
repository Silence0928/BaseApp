#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#不做预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#忽略警告
-ignorewarnings
##apk 包内所有 class 的内部结构
#-dump class_files.txt
##未混淆的类和成员
-printseeds seeds.txt
##列出从 apk 中删除的代码
-printusage unused.txt
##混淆前后的映射
-printmapping mapping.txt
-keepattributes *Annotation*
#如果有引用v4包可以添加下面这行
-dontwarn android.support.**
#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
   native <methods>;
}
# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
 native <methods>;
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#保持 Serializable 不被混淆并且enum 类也不被混淆
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
#保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
#不混淆资源类
-keepclassmembers class **.R$* {
 public static <fields>;
}
#不混淆H5交互
-keepattributes *JavascriptInterface*
 #ClassName是类名，H5_Object是与javascript相交互的object，建议以内部类形式书写
-keepclassmembers class **.ClassName$H5_Object{*;}
#如果使用有Gson，则添加以下配置
-keepattributes Signature
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#====== Bugly ======
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#AROUTER
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
}
# 不需混淆的Android类
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**
# banner 的混淆代码
-keep class com.youth.banner.** {*;}

# Gson
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
# fastjson
-keep class com.alibaba.** {*;}
-keep,allowobfuscation,allowshrinking class * extends com.alibaba.fastjson.TypeReference
# 项目中用到的实体类
-keep class com.stas.whms.bean.** {*;}
-keep class com.lib_common.net.rxhttp.response.** {*;}
# IScan
-keep interface android.os.IScanListener {*;}
-keep interface * extends android.os.IScanListener
-keep class * implements android.os.IScanListener
-keep interface android.os.IScanListener2 {*;}
-keep interface * extends android.os.IScanListener2
-keep class * implements android.os.IScanListener2