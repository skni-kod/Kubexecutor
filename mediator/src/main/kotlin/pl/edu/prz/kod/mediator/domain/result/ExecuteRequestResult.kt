package pl.edu.prz.kod.mediator.domain.result

import org.http4k.core.Status
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse

sealed class ExecuteRequestResult {
    data class Success(val codeResponse: CodeResponse) : ExecuteRequestResult()

    sealed class Failure(val message: String) : ExecuteRequestResult() {
        class NoRunnerAvailable :
            Failure("There are currently no runners available")

        class NoReplyFromRunner :
            Failure("No reply received from runner")

        class ExecutionTimeout :
            Failure("Code execution timeout")

        data class ErrorReplyFromRunner(
            val errorResponse: ErrorResponse,
            val statusCode: Status
        ) :
            Failure(errorResponse.message)
    }
}