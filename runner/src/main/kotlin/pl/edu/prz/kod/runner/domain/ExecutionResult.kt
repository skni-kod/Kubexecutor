package pl.edu.prz.kod.runner.domain

sealed class ExecutionResult {
    data class Success(val stdout: String, val stdErr: String, val exitCode: Int) :
        ExecutionResult()

    sealed class Failure(val message: String) : ExecutionResult() {

        class ProcessTimedOutError(timeout: Long) :
            Failure("Code execution timed out after $timeout ms")

        class CompilationTimedOutError(timeout: Long):
            Failure("Code compilation timed out after $timeout ms")

        class CompilationFailedError(error: String) :
            Failure("Compilation error occurred: $error")
    }
}