package pl.edu.prz.kod.domain

import java.util.concurrent.TimeUnit

sealed class ExecutionResult {
    data class Success(val stdout: String, val stdErr: String, val exitCode: Int) :
        ExecutionResult()

    sealed class Failure(val message: String) : ExecutionResult() {

        class ProcessTimedOutError(timeout: Long, timeoutUnit: TimeUnit) :
            Failure("Code execution timed out after $timeout $timeoutUnit")

        class CompilationTimedOutError(timeout: Long, timeoutUnit: TimeUnit):
            Failure("Code compilation timed out after $timeout $timeoutUnit")

        class CompilationFailedError(error: String) :
            Failure("Compilation error occurred: $error")
    }
}