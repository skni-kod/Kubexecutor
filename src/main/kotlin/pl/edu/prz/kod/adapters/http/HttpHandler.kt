package pl.edu.prz.kod.adapters.http

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.adapters.http.dto.*
import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*

class HttpHandler {
    private val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
    private val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)

    private val requestLens = Jackson.autoBody<CodeRequest>().toLens()
    private val responseLens = Jackson.autoBody<CodeResponse>().toLens()

    private val routesContract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
    }

    val exceptionCatchingHandler: RoutingHttpHandler = ServerFilters.CatchAll { exception ->
        logEvent(
            ExceptionEvent(exception)
        )
        handleExceptions(exception)
    }.then(routes(routesContract))

    val eventsHandler =
        ResponseFilters.ReportHttpTransaction {
            logEvent(
                IncomingHttpRequestEvent(
                    uri = it.request.uri,
                    status = it.response.status.code,
                    duration = it.duration.toMillis()
                )
            )
        }.then(exceptionCatchingHandler)

    val tracingHandler = ServerFilters.RequestTracing()
        .then(eventsHandler)

    private fun executeRoute(): ContractRoute {
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
            logEvent(
                ReceivedCodeRequestEvent(
                    code = codeRequest.base64Code,
                    language = codeRequest.language
                )
            )
            when (val decodingResult = codeRequest.decode(base64Decoder)) {
                is DecodingResult.Successful -> {
                    val code = decodingResult.code
                    logEvent(
                        DecodedCodeEvent(
                            code = code.textValue,
                            language = code.language
                        )
                    )
                    executeDecoded(code)
                }
                is DecodingResult.Failure -> handleDecodingErrors(decodingResult)
            }
        }

        return spec to ::execute
    }

    private fun executeDecoded(code: Code): Response {
        val result = executorOrchestrator.execute(code)
        return when (result) {
            is ExecutionResult.Success -> {
                logEvent(
                    ExecutionSuccessfulEvent(
                        stdout = result.stdout,
                        stdErr = result.stdErr,
                        exitCode = result.exitCode
                    )
                )
                responseLens.inject(result.encode(), Response(OK))
            }

            is ExecutionResult.Failure -> handleExecutionErrors(result)
        }
    }

}