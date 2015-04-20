# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/pikaurd/Developer/Frameworks/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.tencent.mm.sdk.** {
    *;
}
-keep class com.sina.weibo.sdk.widget.** {
    *;
}
-keep class retrofit.** { *; }

-keep class com.zuijiao.android.zuijiao.network.** { *; }

-keepattributes Signature

-dontwarn com.squareup.okhttp.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn java.lang.invoke**
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn android.test.**

-dontwarn com.sina.weibo.sdk.widget**
