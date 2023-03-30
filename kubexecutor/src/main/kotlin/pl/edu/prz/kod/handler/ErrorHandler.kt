package pl.edu.prz.kod.handler

import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import pl.edu.prz.kod.error.LanguageNotImplementedError
import pl.edu.prz.kod.error.ProcessTimedOutError

fun StatusPagesConfig.handleErrors() {
    exception<RequestValidationException> { call, cause ->
        call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
    }
    exception<LanguageNotImplementedError> { call, cause ->
        call.respondText(text = "501: ${cause.message}", status = HttpStatusCode.NotImplemented)
    }
    exception<ProcessTimedOutError> { call, cause ->
        call.respondText(text="408: ${cause.message}", status= HttpStatusCode.RequestTimeout)
    }
}