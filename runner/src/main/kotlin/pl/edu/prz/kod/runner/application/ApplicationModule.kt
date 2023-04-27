package pl.edu.prz.kod.runner.application

import org.koin.dsl.module
import pl.edu.prz.kod.runner.domain.executor.ExecutorOrchestrator
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort
import java.util.Base64
import com.fasterxml.jackson.databind.ObjectMapper

val applicationModule = module {
    single<Base64.Decoder> { Base64.getDecoder() }
    single<ExecutorOrchestratorPort> { ExecutorOrchestrator() }
    single { ObjectMapper() }
}