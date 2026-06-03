package com.singlestore.intellij.lsp

import com.intellij.ide.plugins.PluginManager
import com.singlestore.intellij.settings.SingleStoreLspSettings

object SingleStoreInitializationOptions {
    private val pluginVersion: String
        get() = PluginManager.getPluginByClass(SingleStoreInitializationOptions::class.java)?.version ?: "unknown"
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
