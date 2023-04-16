package pl.edu.prz.kod.adapters.http

import org.http4k.core.Response
import org.http4k.core.Status
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.LanguageNotImplementedError
import pl.edu.prz.kod.domain.ProcessTimedOutError

fun handleExecutionErrors(result: ExecutionResult.Failure): Response = when (result) {
    is ExecutionResult.Failure.ProcessTimedOutError -> Response(Status.REQUEST_TIMEOUT)
    is ExecutionResult.Failure.CompilationFailedError -> Response(Status.BAD_REQUEST)
}

// TODO Exchange to ExecutionResult.Failure handling
fun handleExceptions(throwable: Throwable): Response = when (throwable) {
    is LanguageNotImplementedError -> languageNotImplementedResponse()
    is ProcessTimedOutError -> processTimedOutResponse()
    else -> Response(Status.INTERNAL_SERVER_ERROR)
}

private fun processTimedOutResponse(): Response =
    Response(Status.REQUEST_TIMEOUT)

private fun languageNotImplementedResponse(): Response =
    Response(Status.NOT_IMPLEMENTED).body("Language support is not implemented!")