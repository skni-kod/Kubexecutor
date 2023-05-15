package pl.edu.prz.kod.runner.adapters

import org.koin.dsl.module
import pl.edu.prz.kod.runner.adapters.http.ErrorHandler

val adaptersModule = module {
    single { ErrorHandler() }
}