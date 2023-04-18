package pl.edu.prz.kod.application

import org.http4k.server.Netty
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import pl.edu.prz.kod.adapters.http.HttpHandler
import pl.edu.prz.kod.domain.domainModule

fun main() {
    startKoin {
        modules(
            applicationModule,
            domainModule,

        )
    }

    HttpHandler()
        .tracingHandler
        .asServer(Netty(port = 8080))
        .start()

}