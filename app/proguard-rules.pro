# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\littl_000\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
-keep class org.joda.time.** {*;}
-keep public class com.google.android.gms.* { public *; }
-keepnames class org.apache.** {*;}
-keep public class org.apache.** {*;}
-dontwarn com.google.android.gms.**

## GSON 2.2.4 specific rules ##

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#Don't obfuscate member names for json classes - these are used for deserializing
-keepclassmembers class com.codeforgvl.trolleytrackerclient.models.json.** {*;}