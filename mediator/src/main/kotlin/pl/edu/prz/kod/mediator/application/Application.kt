package pl.edu.prz.kod.mediator.application

import org.http4k.client.OkHttp
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.security.OAuthPersistence
import org.http4k.security.OAuthProvider
import org.http4k.security.google
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.mediator.OAUTH_CALLBACK_PATH
import pl.edu.prz.kod.mediator.adapters.http.ApplicationStartedEvent
import pl.edu.prz.kod.mediator.adapters.http.ErrorHandler
import pl.edu.prz.kod.mediator.adapters.http.MediatorHttpHandler
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.adapters.http.oauth.InMemoryOAuthPersistence
import pl.edu.prz.kod.mediator.domain.RunnerManager
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort
import java.time.Clock

fun main() {
    startKoin {
        modules(
            module {
                single { Lenses() }
                single { Configuration() }
                single<HttpHandler> { OkHttp() }
                singleOf(::ErrorHandler)
                single<OAuthPersistence> {
                    val configuration: Configuration = get()
                    InMemoryOAuthPersistence(Clock.systemUTC(), configuration.frontendHttpUrl, configuration.jwtSecret)
                }
                single {
                    val configuration: Configuration = get()
                    OAuthProvider.google(
                        get(),
                        Credentials(configuration.oAuthClientId, configuration.oAuthClientSecret),
                        Uri.of("${configuration.backendHttpUrl}${OAUTH_CALLBACK_PATH}"),
                        get(),
                        listOf("email", "profile")
                    )
                }
                singleOf(::RunnerManager) bind RunnerManagerPort::class
                single {
                    val configuration: Configuration = get()
                    MediatorHttpHandler(
                        get(),
                        get(),
                        get(),
                        get(),
                        configuration.frontendHttpUrl
                    )
                }
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