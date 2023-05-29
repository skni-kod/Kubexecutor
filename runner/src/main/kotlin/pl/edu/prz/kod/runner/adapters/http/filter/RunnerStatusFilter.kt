package pl.edu.prz.kod.runner.adapters.http.filter

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Status
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.domain.RunnerStatus
import pl.edu.prz.kod.runner.adapters.http.ErrorHandler
import pl.edu.prz.kod.common.EXECUTE_PATH

class RunnerStatusFilter(val statusGetter: () -> RunnerStatus) : Filter {
    private val errorHandler by inject<ErrorHandler>(ErrorHandler::class.java)

    override fun invoke(next: HttpHandler): HttpHandler = { request ->
        if (statusGetter() == RunnerStatus.READY || request.uri.path != EXECUTE_PATH) {
            next(request)
        } else {
            errorHandler.handleRequestRejectedError("Runner not in ready status", Status.SERVICE_UNAVAILABLE)
        }
    }
}