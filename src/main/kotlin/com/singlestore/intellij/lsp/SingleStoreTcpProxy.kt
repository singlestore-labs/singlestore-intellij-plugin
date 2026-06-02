package com.singlestore.intellij.lsp

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

internal class SingleStoreTcpProxy(private val targetHost: String, private val targetPort: Int) : LocalPortProxy {
    private val server = ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"))
    private val running = AtomicBoolean(true)
    private val executor = Executors.newCachedThreadPool()

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
        localSocket.use { inbound ->
            Socket(targetHost, targetPort).use { outbound ->
                val up = executor.submit {
                    inbound.getInputStream().transferTo(outbound.getOutputStream())
                }
                val down = executor.submit {
                    outbound.getInputStream().transferTo(inbound.getOutputStream())
                }
                up.get()
                down.get()
            }
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
