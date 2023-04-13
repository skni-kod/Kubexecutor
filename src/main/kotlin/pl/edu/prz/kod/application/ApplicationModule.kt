package pl.edu.prz.kod.application

import org.koin.dsl.module
import pl.edu.prz.kod.domain.executor.ExecutorOrchestrator
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.Base64

val applicationModule = module {
    single<Base64.Decoder> { Base64.getDecoder() }
    single<ExecutorOrchestratorPort> { ExecutorOrchestrator() }
}