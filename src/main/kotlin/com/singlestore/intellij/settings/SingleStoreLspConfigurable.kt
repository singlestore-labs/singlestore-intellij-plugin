package com.singlestore.intellij.settings

import com.intellij.openapi.options.SearchableConfigurable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JTextField

class SingleStoreLspConfigurable : SearchableConfigurable {
    private val protocol = JComboBox(SingleStoreServerProtocol.entries.toTypedArray())
    private val serverAddress = JTextField()
    private val dbHost = JTextField()
    private val dbPort = JTextField()
    private val dbUsername = JTextField()
    private val dbPassword = JPasswordField()
    private val dbName = JTextField()
    private val dbSsl = JCheckBox("Use SSL")

    private val panel = JPanel(GridBagLayout()).apply {
        var row = 0
        addRow(row++, "Server protocol", protocol)
        addRow(row++, "Server address", serverAddress)
        addRow(row++, "Database host", dbHost)
        addRow(row++, "Database port", dbPort)
        addRow(row++, "Database username", dbUsername)
        addRow(row++, "Database password", dbPassword)
        addRow(row++, "Database name", dbName)

        add(
            dbSsl,
            GridBagConstraints().apply {
                gridx = 1
                gridy = row
                weightx = 1.0
                anchor = GridBagConstraints.WEST
                fill = GridBagConstraints.NONE
                insets = Insets(0, 0, 8, 0)
            },
        )

        add(
            JLabel("TCP uses host:port. WebSocket uses ws://... or wss://..."),
            GridBagConstraints().apply {
                gridx = 0
                gridy = row + 1
                gridwidth = 2
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(8, 0, 0, 0)
            },
        )
    }

    private fun JPanel.addRow(row: Int, label: String, component: JComponent) {
        add(
            JLabel(label),
            GridBagConstraints().apply {
                gridx = 0
                gridy = row
                insets = Insets(0, 0, 8, 8)
                anchor = GridBagConstraints.WEST
            },
        )
        add(
            component,
            GridBagConstraints().apply {
                gridx = 1
                gridy = row
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(0, 0, 8, 0)
            },
        )
    }

    override fun getId(): String = "com.singlestore.intellij.settings"

    override fun getDisplayName(): String = "SingleStore Language Server"

    override fun createComponent(): JComponent = panel

    override fun isModified(): Boolean {
        val state = SingleStoreLspSettings.getInstance().state
        return state.protocol != protocol.selectedItem ||
            state.serverAddress != serverAddress.text ||
            state.dbHost != dbHost.text ||
            state.dbPort != dbPort.text.toIntOrNull() ||
            state.dbUsername != dbUsername.text ||
            state.dbPassword != String(dbPassword.password) ||
            state.dbName != dbName.text ||
            state.dbSsl != dbSsl.isSelected
    }

    override fun apply() {
        val state = SingleStoreLspSettings.getInstance().state
        state.protocol = protocol.selectedItem as SingleStoreServerProtocol
        state.serverAddress = serverAddress.text.trim()
        state.dbHost = dbHost.text.trim()
        state.dbPort = dbPort.text.toIntOrNull() ?: 3306
        state.dbUsername = dbUsername.text.trim()
        state.dbPassword = String(dbPassword.password)
        state.dbName = dbName.text.trim()
        state.dbSsl = dbSsl.isSelected
    }

    override fun reset() {
        val state = SingleStoreLspSettings.getInstance().state
        protocol.selectedItem = state.protocol
        serverAddress.text = state.serverAddress
        dbHost.text = state.dbHost
        dbPort.text = state.dbPort.toString()
        dbUsername.text = state.dbUsername
        dbPassword.text = state.dbPassword
        dbName.text = state.dbName
        dbSsl.isSelected = state.dbSsl
    }
}
