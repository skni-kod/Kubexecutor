package pl.edu.prz.kod.mediator.adapters.http

import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.contract.security.AuthCodeOAuthSecurity
import org.http4k.core.*
import org.http4k.filter.CorsPolicy
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.lens.Header
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.OAuthProvider
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.mediator.application.Configuration
import pl.edu.prz.kod.mediator.domain.ExecutionContext
import pl.edu.prz.kod.mediator.domain.result.ExecuteRequestResult
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort

class MediatorHttpHandler(
    private val errorHandler: ErrorHandler,
    private val runnerManager: RunnerManagerPort,
    private val lenses: Lenses,
    private val oAuthProvider: OAuthProvider,
    private val contexts: RequestContexts,
    private val configuration: Configuration
) {

    private val routesContract = routes(
        "/oauth/callback" bind Method.GET to oAuthProvider.callback,
        contract {
            renderer = OpenApi2(ApiInfo("Kubexecutor Mediator API", "v1.0"), Jackson)
            descriptionPath = "/openapi.json"
            security = AuthCodeOAuthSecurity(oAuthProvider)
            routes += executeRoute()
            routes += authorizeRoute()
        }
    )

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

    val tracingHandler: RoutingHttpHandler = ServerFilters.InitialiseRequestContext(contexts)
        .then(ServerFilters.RequestTracing())
        .then(ServerFilters.Cors(CorsPolicy.UnsafeGlobalPermissive))
        .then(eventsHandler)

    private fun authorizeRoute(): ContractRoute {
        val spec = "/authenticate" bindContract Method.GET

        return spec to { _ -> Response(Status.TEMPORARY_REDIRECT)
            .with(Header.LOCATION of Uri.of(configuration.frontendHttpUrl))
        }
    }

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
                    stdOut = "hello,world!",
                    stdErr = "",
                    exitCode = 0
                )
            )
        } bindContract Method.POST

        fun execute() = { request: Request ->
            val codeRequest = lenses.executeRequestLens.extract(request)
            val context = ExecutionContext(contexts[request]["email"]!!)
            when (val executeRequestResult = runnerManager.execute(codeRequest, context)) {
                is ExecuteRequestResult.Success ->
                    lenses.executeResponseLens.inject(executeRequestResult.codeResponse, Response(Status.OK))
                is ExecuteRequestResult.Failure ->
                    errorHandler.handleExecuteRequestError(executeRequestResult)
            }
        }

        return spec to ::execute
    }
}