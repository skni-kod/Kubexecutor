package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.runner.application.EnvironmentVariable
import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.runner.domain.Language
import java.io.File
import java.util.concurrent.TimeUnit

sealed class AbstractExecutor(language: Language) {
    val timeout = EnvironmentVariable.getSystemCommandTimeout(5000)

    abstract fun execute(code: String): ExecutionResult

    protected fun runSystemCommand(command: String, workDir: File = File("/app")): Process {
        val systemCommand = listOf("bash", "-c", command)
        return ProcessBuilder(systemCommand)
            .directory(workDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
    }

    protected fun Process.timedOut(): Boolean =
        !this.waitFor(timeout, TimeUnit.MILLISECONDS)

}