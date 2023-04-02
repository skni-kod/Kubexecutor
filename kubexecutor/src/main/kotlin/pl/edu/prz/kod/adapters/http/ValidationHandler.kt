package pl.edu.prz.kod.adapters.http

import io.ktor.server.plugins.requestvalidation.*
import pl.edu.prz.kod.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.domain.Language

fun RequestValidationConfig.validateRequest() {
    validate<CodeRequest> { request ->
        if (Language.from(request.language) == null) {
            ValidationResult.Invalid("Language [${request.language}] support is not implemented!")
        } else ValidationResult.Valid
    }
}