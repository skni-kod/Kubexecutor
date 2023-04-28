package pl.edu.prz.kod.runner.adapters.http

import org.http4k.core.Uri
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.Event
import org.http4k.events.EventFilters
import org.http4k.events.then
import org.http4k.format.Jackson
import pl.edu.prz.kod.runner.domain.Language

val logEvent =
    EventFilters.AddTimestamp()
        .then(EventFilters.AddEventName())
        .then(EventFilters.AddZipkinTraces())
        .then(AutoMarshallingEvents(Jackson))

data class HttpRequestEvent(val uri: Uri, val status: Int, val duration: Long) : Event
data class ReceivedCodeRequestEvent(val code: String, val language: String): Event
data class DecodedCodeEvent(val code: String, val language: Language): Event
data class ExecutionSuccessfulEvent(val stdout: String, val stdErr: String, val exitCode: Int): Event
data class ExecutionFailedEvent(val message: String): Event
data class LanguageNotImplementedEvent(val language: String): Event
data class ExceptionEvent(val exception: Throwable): Event
data class ApplicationStartedEvent(val port: Int, val message: String = "Application started"): Event