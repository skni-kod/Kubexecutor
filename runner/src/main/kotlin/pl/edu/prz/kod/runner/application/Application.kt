package pl.edu.prz.kod.runner.application

import org.http4k.server.asServer
import org.koin.core.context.startKoin
import pl.edu.prz.kod.runner.adapters.http.ApplicationStartedEvent
import pl.edu.prz.kod.runner.adapters.http.HttpHandler
import pl.edu.prz.kod.runner.adapters.http.logEvent
import pl.edu.prz.kod.runner.domain.domainModule

fun main() {
    startKoin {
        modules(
            applicationModule,
            domainModule
        )
    }

    val httpPort = EnvironmentVariable.getHttpPort(8080)

    HttpHandler()
        .tracingHandler
        .asServer(pl.edu.prz.kod.runner.adapters.http.CustomUndertow(port = httpPort))
        .start()

    logEvent(
        ApplicationStartedEvent(httpPort)
    )
}