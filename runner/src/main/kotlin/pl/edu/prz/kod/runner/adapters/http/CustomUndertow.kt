package pl.edu.prz.kod.runner.adapters.http

import io.undertow.Undertow
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import org.http4k.core.HttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Http4kUndertowHttpHandler
import org.http4k.server.ServerConfig
import pl.edu.prz.kod.common.EXECUTE_PATH
import pl.edu.prz.kod.common.STATUSES_REQUIRING_RESTART
import java.net.InetSocketAddress
import kotlin.system.exitProcess

class CustomUndertow(
    val port: Int = 8000
) : ServerConfig {
    override fun toServer(http: HttpHandler): Http4kServer {
        val httpHandler = (http).let(::Http4kUndertowHttpHandler)
            .let(::BlockingHandler)
            .let(::RunnerShuttingHandler)

        return object : Http4kServer {
            val server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setWorkerThreads(1)
                .setHandler(httpHandler).build()

            override fun start() = apply { server.start() }

            override fun stop() = apply { server.stop() }

            override fun port(): Int = when {
                port > 0 -> port
                else -> (server.listenerInfo[0].address as InetSocketAddress).port
            }
        }
    }
}

class RunnerShuttingHandler(private val handler: io.undertow.server.HttpHandler) : io.undertow.server.HttpHandler {
    override fun handleRequest(exchange: HttpServerExchange?) {
        exchange?.addExchangeCompleteListener { innerExchange, nextListener ->
            if (innerExchange.requestPath == EXECUTE_PATH &&
                STATUSES_REQUIRING_RESTART.map { it.code }.contains(innerExchange.statusCode)) {
                innerExchange.endExchange()
                exitProcess(0)
            }
            nextListener.proceed()
        }

        handler.handleRequest(exchange)
    }
}