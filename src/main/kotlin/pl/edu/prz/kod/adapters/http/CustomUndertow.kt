package pl.edu.prz.kod.adapters.http

import io.undertow.Undertow
import io.undertow.UndertowOptions.ENABLE_HTTP2
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import io.undertow.server.handlers.GracefulShutdownHandler
import org.http4k.core.HttpHandler
import org.http4k.server.ServerConfig.StopMode
import org.http4k.server.Http4kServer
import org.http4k.server.Http4kUndertowHttpHandler
import org.http4k.server.ServerConfig
import java.net.InetSocketAddress

class CustomUndertow(
    val port: Int = 8000,
    val enableHttp2: Boolean,
    override val stopMode: StopMode = StopMode.Immediate
) : ServerConfig {
    constructor(port: Int = 8000) : this(port, false)

    override fun toServer(http: HttpHandler): Http4kServer {
        val httpHandler =
            (http).let(::Http4kUndertowHttpHandler).let(::BlockingHandler).let { handler ->
                if (stopMode is StopMode.Graceful) {
                    GracefulShutdownHandler(handler)
                } else {
                    handler
                }
            }.let(::RunnerShuttingHandler)

        return object : Http4kServer {
            val server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setWorkerThreads(1)
                .setServerOption(ENABLE_HTTP2, enableHttp2)
                .setHandler(httpHandler).build()

            override fun start() = apply { server.start() }

            override fun stop() = apply {
                (httpHandler as? GracefulShutdownHandler)?.apply {
                    shutdown()
                    awaitShutdown((stopMode as StopMode.Graceful).timeout.toMillis())
                }
                server.stop()
            }

            override fun port(): Int = when {
                port > 0 -> port
                else -> (server.listenerInfo[0].address as InetSocketAddress).port
            }
        }
    }
}

class RunnerShuttingHandler(private val handler: io.undertow.server.HttpHandler) : io.undertow.server.HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange?) {
        exchange?.addExchangeCompleteListener { exchange, nextListener ->
            if (exchange.requestPath == "/execute") {
                exchange.endExchange()
                System.exit(0)
            }
            nextListener.proceed()
        }

        handler.handleRequest(exchange)
    }
}