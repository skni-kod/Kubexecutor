package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.runner.application.Configuration
import pl.edu.prz.kod.runner.domain.ExecutionResult
import java.io.File
import java.util.concurrent.TimeUnit

sealed class AbstractExecutor(
    protected val configuration: Configuration
) {
    abstract fun execute(code: String): ExecutionResult

    protected fun runSystemCommand(command: String, workDir: File = File("/app")): Process =
        ProcessBuilder(listOf("sh", "-c", command))
            .directory(workDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

    protected fun Process.timedOut(): Boolean =
        !this.waitFor(configuration.systemCommandTimeout, TimeUnit.MILLISECONDS)

    protected fun getEscapedCode(code: String): String =
        code.replace("\\", "\\\\")
            .replace("\"", "\\\"")
}