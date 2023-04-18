package pl.edu.prz.kod.adapters.http

import org.http4k.core.Response
import org.http4k.core.Status
import pl.edu.prz.kod.adapters.http.dto.DecodingResult
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.ProcessTimedOutError

fun handleDecodingErrors(result: DecodingResult.Failure): Response = when (result) {
    is DecodingResult.Failure.LanguageNotImplementedResult -> {
        logEvent(
            LanguageNotImplementedEvent(result.language)
        )
        languageNotImplementedResponse(result.language)
    }
}

fun handleExecutionErrors(result: ExecutionResult.Failure): Response {
    logEvent(
        ExecutionFailedEvent(result.message)
    )
    return when (result) {
        is ExecutionResult.Failure.ProcessTimedOutError -> Response(Status.REQUEST_TIMEOUT)
        is ExecutionResult.Failure.CompilationFailedError -> Response(Status.BAD_REQUEST)
    }
}

fun handleExceptions(throwable: Throwable): Response = when (throwable) {
    is ProcessTimedOutError -> processTimedOutResponse()
    else -> Response(Status.INTERNAL_SERVER_ERROR)
}

private fun processTimedOutResponse(): Response =
    Response(Status.REQUEST_TIMEOUT)

private fun languageNotImplementedResponse(language: String): Response =
    Response(Status.NOT_IMPLEMENTED).body("Language [$language] support is not implemented!")