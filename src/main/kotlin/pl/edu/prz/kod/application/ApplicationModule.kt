package pl.edu.prz.kod.application

import org.koin.dsl.module
import pl.edu.prz.kod.domain.executor.ExecutorOrchestrator
import java.util.Base64

val applicationModule = module {
    single { Base64.getDecoder() }
    single { ExecutorOrchestrator() }
}