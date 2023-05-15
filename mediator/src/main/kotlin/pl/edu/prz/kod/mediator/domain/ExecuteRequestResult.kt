package pl.edu.prz.kod.mediator.domain

import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse

sealed class ExecuteRequestResult {
    data class Success(val codeResponse: CodeResponse) : ExecuteRequestResult()

    sealed class Failure(val message: String) : ExecuteRequestResult() {
        class NoRunnerAvailable :
            Failure("There are no available runners currently")

        class NoReplyFromRunner :
            Failure("No reply received from runner")

        data class ErrorReplyFromRunner(
            val errorResponse: ErrorResponse,
            val statusCode: Int,
            val statusDescription: String
        ) :
            Failure(errorResponse.message)
    }
}