package com.singlestore.intellij.lsp

import com.singlestore.intellij.settings.SingleStoreLspSettings

object SingleStoreInitializationOptions {
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
                "name" to "intellij-singlestore",
                "version" to "0.1.0",
            ),
        )
    }
}
