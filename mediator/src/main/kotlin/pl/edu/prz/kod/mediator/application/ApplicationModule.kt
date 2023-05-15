package pl.edu.prz.kod.mediator.application


import okhttp3.OkHttpClient
import org.koin.dsl.module

val applicationModule = module {
    single { OkHttpClient() }
}