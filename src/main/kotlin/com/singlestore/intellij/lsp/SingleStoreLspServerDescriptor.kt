package com.singlestore.intellij.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspCommunicationChannel
import com.intellij.platform.lsp.api.LspServerListener
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.singlestore.intellij.settings.SingleStoreLspSettings
import com.singlestore.intellij.settings.SingleStoreServerProtocol
import org.eclipse.lsp4j.ConfigurationItem

class SingleStoreLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "SingleStore Language Server") {
    private var proxy: LocalPortProxy? = null

    override val lspCommunicationChannel: LspCommunicationChannel
        get() {
            proxy?.close()

            val state = SingleStoreLspSettings.getInstance().state
            proxy = when (state.protocol) {
                SingleStoreServerProtocol.TCP -> {
                    val tcpTarget = SingleStoreAddressParsers.parseTcpAddress(state.serverAddress)
                    SingleStoreTcpProxy(tcpTarget.host, tcpTarget.port)
                }

                SingleStoreServerProtocol.WEBSOCKET -> {
                    val wsTarget = SingleStoreAddressParsers.parseWebSocketAddress(state.serverAddress)
                    SingleStoreWsProxy(wsTarget)
                }
            }

            return LspCommunicationChannel.Socket(requireNotNull(proxy).localPort, startProcess = false)
        }

    override fun isSupportedFile(file: VirtualFile): Boolean = file.extension.equals("sql", ignoreCase = true)

    override fun getLanguageId(file: VirtualFile): String = "sql"

    override fun createInitializationOptions(): Any = SingleStoreInitializationOptions.build(SingleStoreLspSettings.getInstance().state)

    override val lspServerListener: LspServerListener = object : LspServerListener {
        override fun serverInitialized(initializeResult: org.eclipse.lsp4j.InitializeResult) = Unit

        override fun serverStopped(unexpected: Boolean) {
            proxy?.close()
            proxy = null
        }
    }

    override fun getWorkspaceConfiguration(item: ConfigurationItem): Any? {
        return if (item.section == "database") {
            SingleStoreInitializationOptions.build(SingleStoreLspSettings.getInstance().state)["database"]
        } else {
            super.getWorkspaceConfiguration(item)
        }
    }
}
