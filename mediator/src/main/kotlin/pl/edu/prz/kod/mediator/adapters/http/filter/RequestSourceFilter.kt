package pl.edu.prz.kod.mediator.adapters.http.filter

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Status
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.mediator.adapters.http.ErrorHandler
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

class RequestSourceFilter : Filter {
    private val errorHandler by inject<ErrorHandler>(ErrorHandler::class.java)
    private val runnerManger by inject<RunnerManagerPort>(RunnerManagerPort::class.java)

    override fun invoke(next: HttpHandler): HttpHandler = { request ->
        if (!runnerManger.getRunnersIpAddresses().contains(request.source?.address)) {
            next(request)
        } else {
            errorHandler.handleRequestRejectedError("Invalid source of the request", Status.FORBIDDEN)
        }
    }
}