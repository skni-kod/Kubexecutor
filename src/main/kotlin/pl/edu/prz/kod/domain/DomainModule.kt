package pl.edu.prz.kod.domain

import org.koin.dsl.module
import pl.edu.prz.kod.domain.executor.JavaExecutor
import pl.edu.prz.kod.domain.executor.NodeExecutor
import pl.edu.prz.kod.domain.executor.PythonExecutor

val domainModule = module {
    single { PythonExecutor() }
    single { JavaExecutor() }
    single { NodeExecutor() }
}