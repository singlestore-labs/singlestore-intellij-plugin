package com.singlestore.intellij.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(name = "SingleStoreLspSettings", storages = [Storage("singlestore-lsp.xml")])
class SingleStoreLspSettings : PersistentStateComponent<SingleStoreLspSettings.State> {
    data class State(
        var serverAddress: String = "127.0.0.1:8080",
        var dbHost: String = "127.0.0.1",
        var dbPort: Int = 3306,
        var dbUsername: String = "root",
        var dbPassword: String = "",
        var dbName: String = "",
        var dbSsl: Boolean = false,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    companion object {
        fun getInstance(): SingleStoreLspSettings = ApplicationManager.getApplication().getService(SingleStoreLspSettings::class.java)
    }
}
