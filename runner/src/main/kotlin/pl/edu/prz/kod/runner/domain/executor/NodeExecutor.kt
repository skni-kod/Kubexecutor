package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.runner.application.Configuration
import pl.edu.prz.kod.runner.domain.ExecutionResult

class NodeJSExecutor(
    configuration: Configuration
): AbstractExecutor(configuration) {

    override fun execute(code: String): ExecutionResult {
        val escapedCode = getEscapedCode(code)
        val process = runSystemCommand("echo \"$escapedCode\" | node")
        if (process.timedOut()) {
            return ExecutionResult.Failure.ProcessTimedOutError(configuration.systemCommandTimeout)
        }
        return ExecutionResult.Success(
            stdOut = process.inputStream.bufferedReader().readText(),
            stdErr = process.errorStream.bufferedReader().readText(),
            exitCode = process.exitValue()
        )
    }
}