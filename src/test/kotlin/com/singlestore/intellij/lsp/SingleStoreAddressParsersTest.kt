package com.singlestore.intellij.lsp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SingleStoreAddressParsersTest {
    @Test
    fun `parses tcp host and port`() {
        val target = SingleStoreAddressParsers.parseTcpAddress("example.com:8080")
        assertEquals("example.com", target.host)
        assertEquals(8080, target.port)
    }

    @Test
    fun `rejects invalid tcp address`() {
        assertFailsWith<IllegalArgumentException> {
            SingleStoreAddressParsers.parseTcpAddress("example.com")
        }
    }

    @Test
    fun `parses websocket uri`() {
        val uri = SingleStoreAddressParsers.parseWebSocketAddress("wss://example.com/lsp")
        assertEquals("wss", uri.scheme)
        assertEquals("example.com", uri.host)
        assertEquals("/lsp", uri.path)
    }

    @Test
    fun `rejects non websocket scheme`() {
        assertFailsWith<IllegalArgumentException> {
            SingleStoreAddressParsers.parseWebSocketAddress("http://example.com")
        }
    }
}
