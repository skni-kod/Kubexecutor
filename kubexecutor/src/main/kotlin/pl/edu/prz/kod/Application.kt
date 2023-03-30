package pl.edu.prz.kod

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.slf4j.event.Level
import pl.edu.prz.kod.domain.CodeRequest
import pl.edu.prz.kod.domain.Language
import java.util.*

val b64Decoder = Base64.getDecoder()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
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
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
        exception<LanguageNotImplementedError> { call, cause ->
            call.respondText(text = "501: ${cause.message}", status = HttpStatusCode.NotImplemented)
        }
        exception<ProcessTimedOutError> { call, cause ->
            call.respondText(text="408: ${cause.message}", status=HttpStatusCode.RequestTimeout)
        }
    }
    install(RequestValidation) {
        validate<CodeRequest> { request ->
            if (Language.from(request.language) == null) {
                ValidationResult.Invalid("Language [${request.language}] support is not implemented!")
            } else ValidationResult.Valid
        }
    }
    configureRouting()
}
