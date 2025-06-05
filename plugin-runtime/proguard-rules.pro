# Keep plugin database + Room
-keep class com.dark.plugin_runtime.database.installed_plugin_db.** { *; }

# PluginManager & dynamic loading
-keep class com.dark.plugin_runtime.PluginManager { *; }
-keep class com.dark.plugin_runtime.engine.PluginExecutionManager { *; }

# For DexClassLoader to reflect class names
-keepnames class * {
    public <init>(android.content.Context);
}

# Avoid obfuscating main class references
-keepclassmembers class * {
    *** getMainClassByName(...);
    *** getPluginFolderByName(...);
}

-keepclassmembers class com.dark.plugin_runtime.** {
    *;
}