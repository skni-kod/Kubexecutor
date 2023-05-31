package pl.edu.prz.kod.mediator.adapters.http

import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.meta
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.contract.security.AuthCodeOAuthSecurity
import org.http4k.core.*
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.security.OAuthProvider
import org.http4k.security.google
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.mediator.adapters.http.oauth.GoogleTokenChecker
import pl.edu.prz.kod.mediator.adapters.http.oauth.InMemoryOAuthPersistence
import pl.edu.prz.kod.mediator.domain.ExecuteRequestResult
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort
import java.time.Clock

class MediatorHttpHandler(
    private val errorHandler: ErrorHandler,
    private val runnerManager: RunnerManagerPort,
    private val lenses: Lenses,
    private val httpHandler: HttpHandler
) {
    val googleClientId = "CLIENT_ID"
    val googleClientSecret = "CLIENT_SECRET"

    val oAuthPersistence = InMemoryOAuthPersistence(Clock.systemUTC(), GoogleTokenChecker(googleClientId))

    val oauthProvider = OAuthProvider.google(
        httpHandler,
        Credentials(googleClientId, googleClientSecret),
        Uri.of("http://localhost:8081/oauth/callback"),
        oAuthPersistence,
        listOf("email", "profile")
    )

    private val routesContract = routes(
        "/oauth/callback" bind Method.GET to oauthProvider.callback,
        contract {
            renderer = OpenApi2(ApiInfo("Kubexecutor Mediator API", "v1.0"), Jackson)
            descriptionPath = "/openapi.json"
            security = AuthCodeOAuthSecurity(oauthProvider)
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

    val tracingHandler: RoutingHttpHandler = ServerFilters.RequestTracing()
        .then(eventsHandler)

    private fun authorizeRoute(): ContractRoute {
        val spec = "/hello" bindContract Method.GET

        return spec to { _ -> Response(Status.OK) }
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