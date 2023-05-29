package pl.edu.prz.kod.runner.application

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.server.asServer
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.runner.adapters.http.*
import pl.edu.prz.kod.runner.domain.executor.ExecutorOrchestrator
import pl.edu.prz.kod.runner.domain.executor.JavaExecutor
import pl.edu.prz.kod.runner.domain.executor.NodeJSExecutor
import pl.edu.prz.kod.runner.domain.executor.PythonExecutor
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort
import java.util.*

fun main() {
    startKoin {
        modules(
            module {
                single { Lenses() }
                single { Configuration() }
                single<Base64.Decoder> { Base64.getDecoder() }
                single { ObjectMapper() }
                singleOf(::ErrorHandler)
                singleOf(::PythonExecutor)
                singleOf(::JavaExecutor)
                singleOf(::NodeJSExecutor)
                singleOf(::ExecutorOrchestrator) bind ExecutorOrchestratorPort::class
                singleOf(::RunnerHttpHandler)
            }
        )
    }

    val configuration = inject<Configuration>(Configuration::class.java).value
    inject<RunnerHttpHandler>(RunnerHttpHandler::class.java).value
        .tracingHandler
        .asServer(CustomUndertow(port = configuration.httpPort))
        .start()
    logEvent(ApplicationStartedEvent(configuration.httpPort))
}