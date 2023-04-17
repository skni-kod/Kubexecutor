package pl.edu.prz.kod.adapters.http

import org.http4k.core.Uri
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.Event
import org.http4k.events.EventFilters
import org.http4k.events.then
import org.http4k.format.Jackson
import pl.edu.prz.kod.domain.Language

val requestEvent =
    EventFilters.AddTimestamp()
        .then(EventFilters.AddEventName())
        .then(EventFilters.AddZipkinTraces())
        .then(AutoMarshallingEvents(Jackson))

data class IncomingHttpRequest(val uri: Uri, val status: Int, val duration: Long) : Event
data class ReceivedCodeRequest(val code: String, val language: String): Event
data class DecodedCode(val code: String, val language: Language): Event
data class ExecutionSuccessful(val stdout: String, val stdErr: String, val exitCode: Int): Event
data class ExecutionFailed(val message: String): Event
data class LanguageNotImplemented(val language: String): Event