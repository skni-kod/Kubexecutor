package pl.edu.prz.kod.domain

import java.util.concurrent.TimeUnit

class LanguageNotImplementedError(language: String) : Error(language)

class ProcessTimedOutError(timeout: Long, timeoutUnit: TimeUnit) : Error("Code request timed out after $timeout $timeoutUnit")