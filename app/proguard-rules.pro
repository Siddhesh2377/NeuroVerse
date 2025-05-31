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
# Ignore references to AWT classes
-dontwarn java.awt.**
-dontwarn com.sun.jna.**

# Keep native methods used by JNA
-keep class com.sun.jna.** { *; }

-keep class org.vosk.** { *; }
-keepclassmembers class * {
    native <methods>;
}

# Gson: keep model classes
-keep class com.dark.neuroverse.neurov.Command { *; }

# Keep annotations & generics info
-keepattributes Signature
-keepattributes *Annotation*

# Gson parser internals
-keep class com.google.gson.** { *; }

# Retrofit models
-keep class retrofit2.** { *; }

# Prevent R8 from stripping models used via reflection
-keep class * implements java.lang.annotation.Annotation { *; }
-keep class * implements java.io.Serializable { *; }
-keep class * implements java.util.List { *; }


# ErrorProne annotations – not needed at runtime
-dontwarn com.google.errorprone.annotations.**

# Java compiler classes – not present on Android runtime
-dontwarn javax.lang.model.**
