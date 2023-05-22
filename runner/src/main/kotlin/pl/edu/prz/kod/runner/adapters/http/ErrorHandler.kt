package pl.edu.prz.kod.runner.adapters.http

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse
import pl.edu.prz.kod.runner.adapters.http.dto.DecodingResult
import pl.edu.prz.kod.runner.domain.ExecutionResult

class ErrorHandler {
    private val errorResponseLens = Jackson.autoBody<ErrorResponse>().toLens()

    fun handleDecodingError(result: DecodingResult.Failure): Response = when (result) {
        is DecodingResult.Failure.LanguageNotImplementedResult -> {
            logEvent(LanguageNotImplementedEvent(result.language))
            errorResponseLens.inject(
                ErrorResponse("Language [${result.language}] support is not implemented!"),
                Response(Status.NOT_IMPLEMENTED)
            )
        }
    }

    fun handleExecutionError(result: ExecutionResult.Failure): Response {
        logEvent(ExecutionFailedEvent(result.message))
        return when (result) {
            is ExecutionResult.Failure.ProcessTimedOutError ->
                errorResponseLens.inject(ErrorResponse(result.message), Response(Status.REQUEST_TIMEOUT))
            is ExecutionResult.Failure.CompilationTimedOutError ->
                errorResponseLens.inject(ErrorResponse(result.message), Response(Status.REQUEST_TIMEOUT))
            is ExecutionResult.Failure.CompilationFailedError ->
                errorResponseLens.inject(ErrorResponse(result.message), Response(Status.BAD_REQUEST))
        }
    }

    fun handleRequestRejectedError(reason: String, status: Status): Response {
        logEvent(RequestRejectedEvent(reason))
        return errorResponseLens.inject(ErrorResponse(reason), Response(status))
    }

    //    Handle future exceptions here
    fun handleException(throwable: Throwable): Response {
        logEvent(ExceptionEvent(throwable))
        return errorResponseLens.inject(
            ErrorResponse("Internal error occurred"),
            Response(Status.INTERNAL_SERVER_ERROR)
        )
    }
}
