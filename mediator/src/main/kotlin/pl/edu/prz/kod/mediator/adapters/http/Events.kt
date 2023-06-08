package pl.edu.prz.kod.mediator.adapters.http

import org.http4k.core.Uri
import org.http4k.events.AutoMarshallingEvents
import org.http4k.events.Event
import org.http4k.events.EventFilters
import org.http4k.events.then
import org.http4k.format.Jackson

val logEvent =
    EventFilters.AddTimestamp()
        .then(EventFilters.AddEventName())
        .then(EventFilters.AddZipkinTraces())
        .then(AutoMarshallingEvents(Jackson))

data class HttpRequestEvent(val uri: Uri, val status: Int, val duration: Long) : Event
data class RunnerReadyEvent(val runnerName: String) : Event
data class RequestAssignedToRunnerEvent(val runnerName: String) : Event
data class TokenAssignedEvent(val email: String) : Event
data class TokenVerifiedEvent(val email: String) : Event
data class ExecutionFailedEvent(val message: String) : Event
data class ExceptionEvent(val exception: Throwable) : Event
data class LogInsertedEvent(val email: String) : Event
data class FailedToInsertLogEvent(val email: String) : Event
data class ApplicationStartedEvent(val port: Int, val message: String = "Application started") : Event
