package pl.edu.prz.kod.domain.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language

class PythonExecutor: AbstractExecutor(Language.PYTHON) {

    override fun execute(code: String): ExecutionResult {
        val escapedCode = code.replace("\"", "\\\"")
        val process = runSystemCommand("echo \"$escapedCode\" | python3")
        return ExecutionResult.Success(
            stdout = process.inputStream.bufferedReader().readText(),
            stdErr = process.errorStream.bufferedReader().readText(),
            exitCode = process.exitValue()
        )
    }

}