package com.singlestore.intellij.lsp

import com.singlestore.intellij.settings.SingleStoreLspSettings
import java.util.Properties

object SingleStoreInitializationOptions {
    private val pluginVersion: String = run {
        val props = Properties()
        SingleStoreInitializationOptions::class.java
            .classLoader
            .getResourceAsStream("version.properties")
            ?.use { props.load(it) }
        props.getProperty("plugin.version", "unknown")
    }
    fun build(state: SingleStoreLspSettings.State): Map<String, Any> {
        val database = linkedMapOf<String, Any>(
            "host" to state.dbHost,
            "port" to state.dbPort,
            "username" to state.dbUsername,
            "password" to state.dbPassword,
            "ssl" to state.dbSsl,
        )

        if (state.dbName.isNotBlank()) {
            database["database"] = state.dbName
        }

        return mapOf(
            "database" to database,
            "client" to mapOf(
                "name" to "Singlestore IntelliJ Extension",
                "version" to pluginVersion,
            ),
        )
    }
}
