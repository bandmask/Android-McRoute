# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Java/tools/proguard/proguard-android.txt
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
-verbose
-keep class com.ropr.mcroute.** { *; }
-keep class * implements com.ropr.mcroute.** { *; }
-keepclassmembers class com.ropr.mcroute.** { *; }

-dontwarn com.google.android.gms.internal.**
-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers class * {
    @android.os.Parcelable <methods>;
}