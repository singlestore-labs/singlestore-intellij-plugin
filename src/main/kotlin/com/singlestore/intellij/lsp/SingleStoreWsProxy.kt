package com.singlestore.intellij.lsp

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

internal class SingleStoreWsProxy(private val targetUri: URI) : LocalPortProxy {
    private val server = ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))
    private val running = AtomicBoolean(true)
    private val executor = Executors.newCachedThreadPool()
    private val httpClient = HttpClient.newHttpClient()

    override val localPort: Int
        get() = server.localPort

    init {
        executor.execute {
            while (running.get()) {
                val localSocket = try {
                    server.accept()
                } catch (_: IOException) {
                    break
                }

                executor.execute {
                    bridge(localSocket)
                }
            }
        }
    }

    private fun bridge(localSocket: Socket) {
        localSocket.use { socket ->
            val output = socket.getOutputStream()
            val listener = object : WebSocket.Listener {
                override fun onOpen(webSocket: WebSocket) {
                    webSocket.request(1)
                }

                override fun onBinary(webSocket: WebSocket, data: ByteBuffer, last: Boolean): CompletionStage<*> {
                    val bytes = ByteArray(data.remaining())
                    data.get(bytes)
                    output.write(bytes)
                    output.flush()
                    webSocket.request(1)
                    return java.util.concurrent.CompletableFuture.completedFuture(null)
                }

                override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletionStage<*> {
                    output.write(data.toString().toByteArray(StandardCharsets.UTF_8))
                    output.flush()
                    webSocket.request(1)
                    return java.util.concurrent.CompletableFuture.completedFuture(null)
                }

                override fun onError(webSocket: WebSocket, error: Throwable) {
                    this.close()
                }
            }

            val webSocket = httpClient.newWebSocketBuilder().buildAsync(targetUri, listener).join()
            val input = socket.getInputStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (running.get()) {
                val read = input.read(buffer)
                if (read < 0) {
                    break
                }
                webSocket.sendBinary(ByteBuffer.wrap(buffer, 0, read), true).join()
            }
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "closed").join()
        }
    }

    override fun close() {
        if (!running.compareAndSet(true, false)) {
            return
        }
        server.close()
        executor.shutdownNow()
    }
}
