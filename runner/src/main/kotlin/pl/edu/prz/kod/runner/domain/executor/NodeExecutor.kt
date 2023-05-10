package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.common.domain.Language
import pl.edu.prz.kod.runner.domain.ExecutionResult

class NodeJSExecutor: AbstractExecutor(Language.NODEJS) {

    override fun execute(code: String): ExecutionResult {
        val escapedCode = getEscapedCode(code)
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