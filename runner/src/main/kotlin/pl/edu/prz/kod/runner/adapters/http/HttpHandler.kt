package pl.edu.prz.kod.runner.adapters.http

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.SERVICE_UNAVAILABLE
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.runner.adapters.http.dto.*
import pl.edu.prz.kod.runner.domain.Code
import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.runner.domain.ExecutorStatus
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort
import java.util.*

class HttpHandler {
    private val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
    private val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)

    private var executorStatus = ExecutorStatus.READY

    private val executeRequestLens = Jackson.autoBody<CodeRequest>().toLens()
    private val executeResponseLens = Jackson.autoBody<CodeResponse>().toLens()
    private val statusResponseLens = Jackson.autoBody<StatusResponse>().toLens()

    private val routesContract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
        routes += statusRoute()
    }

    private val exceptionCatchingHandler: RoutingHttpHandler = ServerFilters.CatchAll { exception ->
        logEvent(
            ExceptionEvent(exception)
        )
        handleException(exception)
    }.then(routes(routesContract))

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

    val tracingHandler: HttpHandler = ServerFilters.RequestTracing()
        .then(eventsHandler)

    private fun executeRoute(): ContractRoute {
        val spec = "/execute" meta {
            summary = "Executes code request"
            receiving(
                executeRequestLens to CodeRequest(
                    base64Code = "cHJpbnQoImhlbGxvLCB3b3JsZCEiKQ==",
                    language = "python"
                )
            )
            returning(
                OK,
                executeResponseLens to CodeResponse(
                    stdout = "hello,world!",
                    stdErr = "",
                    exitCode = 0
                )
            )
        } bindContract Method.POST

        fun execute() = { request: Request ->
            if (executorStatus == ExecutorStatus.READY) {
                executorStatus = ExecutorStatus.EXECUTING
                val codeRequest = executeRequestLens.extract(request)
                logEvent(
                    ReceivedCodeRequestEvent(
                        code = codeRequest.base64Code,
                        language = codeRequest.language
                    )
                )
                val result = when (val decodingResult = codeRequest.decode(base64Decoder)) {
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

                    is DecodingResult.Failure -> handleDecodingError(decodingResult)
                }
                executorStatus = ExecutorStatus.RESTARTING
                result
            } else {
                Response(SERVICE_UNAVAILABLE).body("Executor not in ready status")
            }
        }

        return spec to ::execute
    }

    private fun statusRoute(): ContractRoute {
        val spec = "/status" meta {
            summary = "Gets current status of executor"
            returning(
                OK,
                statusResponseLens to StatusResponse(
                    ExecutorStatus.READY
                )
            )
        } bindContract Method.GET

        fun getStatus() = { _: Request ->
            statusResponseLens.inject(
                StatusResponse(executorStatus),
                if (executorStatus == ExecutorStatus.READY) Response(OK) else Response(SERVICE_UNAVAILABLE)
            )
        }

        return spec to ::getStatus
    }

    private fun executeDecoded(code: Code): Response {
        return when (val result = executorOrchestrator.execute(code)) {
            is ExecutionResult.Success -> {
                logEvent(
                    ExecutionSuccessfulEvent(
                        stdout = result.stdout,
                        stdErr = result.stdErr,
                        exitCode = result.exitCode
                    )
                )
                executeResponseLens.inject(result.encode(), Response(OK))
            }

            is ExecutionResult.Failure -> handleExecutionError(result)
        }
    }

}