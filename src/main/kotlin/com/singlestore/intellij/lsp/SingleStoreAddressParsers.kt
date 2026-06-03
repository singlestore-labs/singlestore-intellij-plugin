package com.singlestore.intellij.lsp

import java.net.URI

internal data class TcpTarget(val host: String, val port: Int)

internal object SingleStoreAddressParsers {
    fun isWebSocketAddress(address: String): Boolean =
        address.trimStart().let { it.startsWith("ws://", ignoreCase = true) || it.startsWith("wss://", ignoreCase = true) }

    fun parseTcpAddress(address: String): TcpTarget {
        val trimmed = address.trim()
        val separator = trimmed.lastIndexOf(':')
        require(separator > 0 && separator < trimmed.length - 1) { "TCP address must use host:port format" }

        val host = trimmed.substring(0, separator).trim()
        val port = trimmed.substring(separator + 1).trim().toIntOrNull()
        require(host.isNotEmpty()) { "TCP address host cannot be empty" }
        require(port != null && port in 1..65535) { "TCP address port must be in range 1..65535" }
        return TcpTarget(host, port)
    }

    fun parseWebSocketAddress(address: String): URI {
        val uri = URI(address.trim())
        require(uri.scheme == "ws" || uri.scheme == "wss") { "WebSocket address must start with ws:// or wss://" }
        require(!uri.host.isNullOrBlank()) { "WebSocket address must include a host" }
        return uri
    }
}
