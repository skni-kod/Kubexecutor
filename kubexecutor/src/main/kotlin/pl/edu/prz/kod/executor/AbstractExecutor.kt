package pl.edu.prz.kod.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import java.util.concurrent.TimeUnit

sealed class AbstractExecutor(language: Language) {
    abstract fun execute(code: String, timeout: Long, timeoutUnit: TimeUnit): ExecutionResult

}