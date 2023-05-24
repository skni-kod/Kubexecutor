package pl.edu.prz.kod.mediator.adapters.http

import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse
import pl.edu.prz.kod.mediator.domain.ExecuteRequestResult

class ErrorHandler {
    private val errorResponseLens = Jackson.autoBody<ErrorResponse>().toLens()

    fun handleExecuteRequestError(requestResult: ExecuteRequestResult.Failure): Response {
        logEvent(ExecutionFailedEvent(requestResult.message))
        return when (requestResult) {
            is ExecuteRequestResult.Failure.NoReplyFromRunner ->
                errorResponseLens.inject(ErrorResponse(requestResult.message), Response(Status.INTERNAL_SERVER_ERROR))

            is ExecuteRequestResult.Failure.NoRunnerAvailable ->
                errorResponseLens.inject(ErrorResponse(requestResult.message), Response(Status.SERVICE_UNAVAILABLE))

            is ExecuteRequestResult.Failure.ErrorReplyFromRunner ->
                errorResponseLens.inject(
                    requestResult.errorResponse,
                    Response(Status(requestResult.statusCode, requestResult.statusDescription))
                )
        }
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
