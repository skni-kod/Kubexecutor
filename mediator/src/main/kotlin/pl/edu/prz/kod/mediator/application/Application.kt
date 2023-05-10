package pl.edu.prz.kod.mediator.application

import org.http4k.server.Netty
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.application.EnvironmentVariable
import pl.edu.prz.kod.mediator.adapters.http.ApplicationStartedEvent
import pl.edu.prz.kod.mediator.adapters.http.HttpHandler
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.domain.domainModule
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

fun main() {
    startKoin {
        modules(
            applicationModule,
            domainModule
        )
    }

    val httpPort = EnvironmentVariable.getHttpPort()

    HttpHandler()
        .tracingHandler
        .asServer(Netty(port = httpPort))
        .start()

    logEvent(ApplicationStartedEvent(httpPort))

    inject<RunnerManagerPort>(RunnerManagerPort::class.java).value.initialize()
}