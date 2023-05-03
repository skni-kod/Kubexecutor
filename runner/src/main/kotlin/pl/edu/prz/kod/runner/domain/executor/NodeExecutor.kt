package pl.edu.prz.kod.domain.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language

class NodeExecutor: AbstractExecutor(Language.NODE) {
    override fun execute(code: String): ExecutionResult {
        val escapedCode = code.replace("\"", "\\\"")
        val process = runSystemCommand("echo \"$escapedCode\" | node")
        if (process.timedOut()) {
            return ExecutionResult.Failure.ProcessTimedOutError(timeout)
        }
        return ExecutionResult.Success(
            stdout = process.inputStream.bufferedReader().readText(),
            stdErr = process.errorStream.bufferedReader().readText(),
            exitCode = process.exitValue()
        )
    }
}