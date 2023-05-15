package pl.edu.prz.kod.mediator.domain

import org.koin.dsl.module
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

val domainModule = module {
    single<RunnerManagerPort> { RunnerManager() }
}