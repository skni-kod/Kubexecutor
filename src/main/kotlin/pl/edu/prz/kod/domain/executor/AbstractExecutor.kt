package pl.edu.prz.kod.domain.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.domain.ProcessTimedOutError
import java.io.File
import java.util.concurrent.TimeUnit

sealed class AbstractExecutor(language: Language) {
    private val timeout = 5L
    private val timeoutUnit = TimeUnit.SECONDS

    abstract fun execute(code: String): ExecutionResult

    protected fun runSystemCommand(command: String, workDir: File = File("/home/mherda")): Process {
        val systemCommand = listOf("bash", "-c", command)
        val process = ProcessBuilder(systemCommand)
                .directory(workDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        if (!process.waitFor(timeout, timeoutUnit)) {
            throw ProcessTimedOutError(timeout, timeoutUnit)
        }
        return process
    }

}