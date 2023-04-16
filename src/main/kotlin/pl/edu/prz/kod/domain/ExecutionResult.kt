package pl.edu.prz.kod.domain

import java.util.concurrent.TimeUnit

sealed class ExecutionResult {
    data class Success(val stdout: String, val stdErr: String, val exitCode: Int) :
        ExecutionResult()

    sealed class Failure: ExecutionResult() {
        class ProcessTimedOutError(timeout: Long, timeoutUnit: TimeUnit) : Failure() {
            val message = "Code request timed out after $timeout $timeoutUnit"
        }

        class CompilationFailedError(error: String): Failure() {
            val message = "Compilation error occurred: $error"
        }
    }
}