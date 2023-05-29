package pl.edu.prz.kod.mediator.adapters.http

import org.http4k.contract.ContractRoute
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.core.*
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.mediator.domain.ExecuteRequestResult
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

class MediatorHttpHandler(
    private val errorHandler: ErrorHandler,
    private val runnerManager: RunnerManagerPort,
    private val lenses: Lenses
) {
    private val routesContract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor Mediator API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
    }

    private val exceptionCatchingHandler: RoutingHttpHandler = ServerFilters
        .CatchAll { errorHandler.handleException(it) }
        .then(routesContract)

    private val eventsHandler =
        ResponseFilters.ReportHttpTransaction {
            logEvent(
                HttpRequestEvent(
                    uri = it.request.uri,
                    status = it.response.status.code,
                    duration = it.duration.toMillis()
                )
            )
        }.then(exceptionCatchingHandler)

    val tracingHandler: RoutingHttpHandler = ServerFilters.RequestTracing()
        .then(eventsHandler)

    private fun executeRoute(): ContractRoute {
        val spec = "/execute" meta {
            summary = "Executes code request"
            receiving(
                lenses.executeRequestLens to CodeRequest(
                    base64Code = "cHJpbnQoImhlbGxvLCB3b3JsZCEiKQ==",
                    language = "python"
                )
            )
            returning(
                Status.OK,
                lenses.executeResponseLens to CodeResponse(
                    stdout = "hello,world!",
                    stdErr = "",
                    exitCode = 0
                )
            )
        } bindContract Method.POST

        fun execute() = { request: Request ->
            val codeRequest = lenses.executeRequestLens.extract(request)
            when (val executeRequestResult = runnerManager.execute(codeRequest)) {
                is ExecuteRequestResult.Success ->
                    lenses.executeResponseLens.inject(executeRequestResult.codeResponse, Response(Status.OK))
                is ExecuteRequestResult.Failure ->
                    errorHandler.handleExecuteRequestError(executeRequestResult)
            }
        }

        return spec to ::execute
    }
}