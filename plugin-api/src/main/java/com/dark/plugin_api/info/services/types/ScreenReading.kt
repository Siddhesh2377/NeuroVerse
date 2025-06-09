package com.dark.plugin_api.info.services.types

import android.content.Context
import com.dark.plugin_api.info.services.PluginService

class ScreenReading(context: Context) : PluginService(context) {
    override fun getServiceType(): ServiceType {
        return ServiceType.SCREEN_READING
    }
}