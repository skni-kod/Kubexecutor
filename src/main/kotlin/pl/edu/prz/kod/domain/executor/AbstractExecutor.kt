package pl.edu.prz.kod.domain.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import java.io.File
import java.util.concurrent.TimeUnit

sealed class AbstractExecutor(language: Language) {
    protected val timeout = 5L
    protected val timeoutUnit = TimeUnit.SECONDS

    abstract fun execute(code: String): ExecutionResult

    protected fun runSystemCommand(command: String, workDir: File = File("/app")): Process {
        val systemCommand = listOf("bash", "-c", command)
        val process = ProcessBuilder(systemCommand)
            .directory(workDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
        return process
    }

    protected fun Process.timedOut(): Boolean = !this.waitFor(timeout, timeoutUnit)

}