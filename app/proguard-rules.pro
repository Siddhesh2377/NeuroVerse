# Gson + Retrofit
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Compose / Lifecycle / Room
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Plugin interface
-keep interface com.dark.plugin_api.info.plugin.Plugin
-keep class com.dark.plugin_api.info.plugin.Plugin { *; }

# PluginRouter models
-keep class com.dark.neuroverse.data.models.** { *; }

# Ensure ViewModels are preserved
-keep class com.dark.neuroverse.viewModel.** { *; }

# Reflection + dynamic loading
-keepattributes Signature,*Annotation*,EnclosingMethod,InnerClasses
-keep class * extends java.lang.annotation.Annotation { *; }

# Suppress unrelated warnings
-dontwarn javax.lang.model.**
-dontwarn com.google.errorprone.annotations.**

# PluginRouter
-keep class com.dark.neuroverse.neurov.mcp.ai.PluginRouter { *; }

# JSON schema constants
-keep class com.dark.neuroverse.neurov.mcp.ai.PluginRouterDataKt
