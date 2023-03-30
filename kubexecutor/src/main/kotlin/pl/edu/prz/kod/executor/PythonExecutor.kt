package pl.edu.prz.kod.executor

import pl.edu.prz.kod.ProcessTimedOutError
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import java.util.concurrent.TimeUnit

class PythonExecutor: AbstractExecutor(Language.PYTHON) {

    override fun execute(code: String, timeout: Long, timeoutUnit: TimeUnit): ExecutionResult {
        val escapedCode = code.replace("\"", "\\\"")
        val baseCommand = "echo \"$escapedCode\" | python3"
        val commandToExecute = listOf("bash", "-c", baseCommand)
        val process = ProcessBuilder(commandToExecute)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        if (!process.waitFor(timeout, timeoutUnit))
            throw ProcessTimedOutError(timeout, timeoutUnit)
        return ExecutionResult(
            stdout = process.inputStream.bufferedReader().readText(),
            stdErr = process.errorStream.bufferedReader().readText(),
            exitCode = process.exitValue()
        )
    }

}