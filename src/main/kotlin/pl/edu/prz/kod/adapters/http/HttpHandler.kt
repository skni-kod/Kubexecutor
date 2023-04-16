package pl.edu.prz.kod.adapters.http

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.adapters.http.dto.*
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*

class HttpHandler {
    private val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
    private val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)

    private val contract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
    }

    val handler: RoutingHttpHandler = ServerFilters.CatchAll { exception ->
        handleExceptions(exception)
    }.then(routes(contract))

    fun executeRoute(): ContractRoute {
        val requestLens = Jackson.autoBody<CodeRequest>().toLens()
        val responseLens = Jackson.autoBody<CodeResponse>().toLens()

        val spec = "/execute" meta {
            summary = "Executes code request"
            receiving(
                requestLens to CodeRequest(
                    base64Code = "cHJpbnQoImhlbGxvLCB3b3JsZCEiKQ==",
                    language = "python"
                )
            )
            returning(
                OK,
                responseLens to CodeResponse(
                    stdout = "hello,world!",
                    stdErr = "",
                    exitCode = 0
                )
            )
        } bindContract Method.POST

        fun execute() = { request: Request ->
            val codeRequest = requestLens.extract(request)
            val code = codeRequest.decode(base64Decoder)
            val result = executorOrchestrator.execute(code)
            when (result) {
                is ExecutionResult.Success -> responseLens.inject(result.encode(), Response(OK))
                is ExecutionResult.Failure -> handleExecutionErrors(result)
            }
        }

        return spec to ::execute
    }

}