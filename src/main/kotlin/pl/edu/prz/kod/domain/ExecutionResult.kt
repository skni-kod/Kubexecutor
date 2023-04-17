package pl.edu.prz.kod.domain

import java.util.concurrent.TimeUnit

sealed class ExecutionResult {
    data class Success(val stdout: String, val stdErr: String, val exitCode: Int) :
        ExecutionResult()

    sealed class Failure(message: String) : ExecutionResult() {
        val message: String = message

        class ProcessTimedOutError(timeout: Long, timeoutUnit: TimeUnit) :
            Failure("Code request timed out after $timeout $timeoutUnit")

        class CompilationFailedError(error: String) :
            Failure("Compilation error occurred: $error")
    }
}