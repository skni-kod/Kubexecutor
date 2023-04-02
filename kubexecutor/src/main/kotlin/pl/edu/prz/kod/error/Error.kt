package pl.edu.prz.kod.error

import java.util.concurrent.TimeUnit

class LanguageNotImplementedError(language: String) : Error(language)

class ProcessTimedOutError(timeout: Long, timeoutUnit: TimeUnit) : Error("Code request timed out after $timeout $timeoutUnit")

class CompilationFailedError(error: String) : Error("Compilation error occurred: $error")