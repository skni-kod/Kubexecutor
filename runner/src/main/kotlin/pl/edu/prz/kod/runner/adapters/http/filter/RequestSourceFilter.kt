package pl.edu.prz.kod.runner.adapters.http.filter

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Status
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.runner.adapters.http.ErrorHandler
import pl.edu.prz.kod.runner.domain.STATUS_PATH

class RequestSourceFilter : Filter {
    private val errorHandler by inject<ErrorHandler>(ErrorHandler::class.java)

    private var mediatorIpAddress: String? = null

    override fun invoke(next: HttpHandler): HttpHandler = { request ->
       if (mediatorIpAddress?.equals(request.source?.address) == true) {
           next(request)
       } else if (mediatorIpAddress == null && request.uri.path == STATUS_PATH) {
           mediatorIpAddress = request.source?.address
           next(request)
       } else {
           errorHandler.handleRequestRejectedError("Invalid source of the request", Status.FORBIDDEN)
       }
    }
}