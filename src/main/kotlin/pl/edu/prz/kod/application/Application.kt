package pl.edu.prz.kod.application

import org.http4k.server.asServer
import org.koin.core.context.startKoin
import pl.edu.prz.kod.adapters.http.ApplicationStartedEvent
import pl.edu.prz.kod.adapters.http.CustomUndertow
import pl.edu.prz.kod.adapters.http.HttpHandler
import pl.edu.prz.kod.adapters.http.logEvent
import pl.edu.prz.kod.domain.domainModule

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
        .asServer(CustomUndertow(port = httpPort))
        .start()

    logEvent(
        ApplicationStartedEvent(httpPort)
    )
}