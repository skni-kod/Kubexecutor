package pl.edu.prz.kod.mediator.application

import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.mediator.adapters.http.ApplicationStartedEvent
import pl.edu.prz.kod.mediator.adapters.http.ErrorHandler
import pl.edu.prz.kod.mediator.adapters.http.MediatorHttpHandler
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.domain.RunnerManager
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

fun main() {
    startKoin {
        modules(
            module {
                single { Lenses() }
                single { Configuration() }
                single<HttpHandler> { OkHttp() }
                singleOf(::ErrorHandler)
                singleOf(::RunnerManager) bind RunnerManagerPort::class
                singleOf(::MediatorHttpHandler)
            }
        )
    }

    val configuration = inject<Configuration>(Configuration::class.java).value
    inject<MediatorHttpHandler>(MediatorHttpHandler::class.java).value
        .tracingHandler
        .asServer(Netty(port = configuration.httpPort))
        .start()
    logEvent(ApplicationStartedEvent(configuration.httpPort))

}