package com.dark.plugin_api.info.services.types

enum class ServiceType(val serviceName: String) {
    NONE("none"),
    SCREEN_READING("Screen_Reading"), // will contain all the services like HW button click and screen text reading
    AUTO_START("Auto_Start")
}