package pl.edu.prz.kod.runner.domain

import org.koin.dsl.module
import pl.edu.prz.kod.runner.domain.executor.JavaExecutor
import pl.edu.prz.kod.runner.domain.executor.PythonExecutor

val domainModule = module {
    single { PythonExecutor() }
    single { JavaExecutor() }
}