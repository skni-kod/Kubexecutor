package pl.edu.prz.kod.runner.adapters.http

import org.http4k.core.Response
import org.http4k.core.Status
import pl.edu.prz.kod.runner.adapters.http.dto.DecodingResult
import pl.edu.prz.kod.runner.domain.ExecutionResult

fun handleDecodingError(result: DecodingResult.Failure): Response = when (result) {
    is DecodingResult.Failure.LanguageNotImplementedResult -> {
        logEvent(
            LanguageNotImplementedEvent(result.language)
        )
        languageNotImplementedResponse(result.language)
    }
}

fun handleExecutionError(result: ExecutionResult.Failure): Response {
    logEvent(
        ExecutionFailedEvent(result.message)
    )
    return when (result) {
        is ExecutionResult.Failure.ProcessTimedOutError -> Response(Status.REQUEST_TIMEOUT).body(result.message)
        is ExecutionResult.Failure.CompilationTimedOutError -> Response(Status.REQUEST_TIMEOUT).body(result.message)
        is ExecutionResult.Failure.CompilationFailedError -> Response(Status.BAD_REQUEST).body(result.message)
    }
}

//    Handle future exceptions here
fun handleException(throwable: Throwable): Response = when (throwable) {
    else -> Response(Status.INTERNAL_SERVER_ERROR)
}

private fun languageNotImplementedResponse(language: String): Response =
    Response(Status.NOT_IMPLEMENTED).body("Language [$language] support is not implemented!")