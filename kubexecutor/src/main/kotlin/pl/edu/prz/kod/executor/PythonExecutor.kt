package pl.edu.prz.kod.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language

class PythonExecutor: AbstractExecutor(Language.PYTHON) {

    override fun execute(code: String): ExecutionResult {
        val escapedCode = code.replace("\"", "\\\"")
        val process = runSystemCommand("echo \"$escapedCode\" | python3")
        return ExecutionResult(
            stdout = process.inputStream.bufferedReader().readText(),
            stdErr = process.errorStream.bufferedReader().readText(),
            exitCode = process.exitValue()
        )
    }

}