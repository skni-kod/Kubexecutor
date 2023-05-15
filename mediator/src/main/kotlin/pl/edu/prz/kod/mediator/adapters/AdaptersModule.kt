package pl.edu.prz.kod.mediator.adapters

import org.koin.dsl.module
import pl.edu.prz.kod.mediator.adapters.http.ErrorHandler

val adaptersModule = module {
    single { ErrorHandler() }
}