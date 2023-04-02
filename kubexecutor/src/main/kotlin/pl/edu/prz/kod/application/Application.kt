package pl.edu.prz.kod.application

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import org.slf4j.event.Level
import pl.edu.prz.kod.adapters.http.HttpHandler
import pl.edu.prz.kod.adapters.http.handleErrors
import pl.edu.prz.kod.adapters.http.validateRequest
import pl.edu.prz.kod.domain.executor.ExecutorOrchestrator

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module, configure = {
        connectionGroupSize = 1
        workerGroupSize = 1
        callGroupSize = 1
    })
        .start(wait = true)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        handleErrors()
    }
    install(RequestValidation) {
        validateRequest()
    }
    HttpHandler(this, ExecutorOrchestrator())
}