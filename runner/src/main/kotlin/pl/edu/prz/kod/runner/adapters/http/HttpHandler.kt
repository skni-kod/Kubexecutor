package pl.edu.prz.kod.runner.adapters.http

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v2.OpenApi2
import org.http4k.core.Status.Companion.SERVICE_UNAVAILABLE
import org.http4k.filter.ResponseFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.runner.adapters.http.dto.*
import pl.edu.prz.kod.runner.domain.Code
import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort
import pl.edu.prz.kod.common.adapters.http.dto.StatusResponse
import pl.edu.prz.kod.common.domain.RunnerStatus
import java.util.*

class HttpHandler {
    private val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
    private val errorHandler by inject<ErrorHandler>(ErrorHandler::class.java)
    private val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)

    private var runnerStatus = RunnerStatus.READY

    private val executeRequestLens = Jackson.autoBody<CodeRequest>().toLens()
    private val executeResponseLens = Jackson.autoBody<CodeResponse>().toLens()
    private val statusResponseLens = Jackson.autoBody<StatusResponse>().toLens()

    private val routesContract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor Runner API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
        routes += statusRoute()
    }

    private val exceptionCatchingHandler: RoutingHttpHandler = ServerFilters
        .CatchAll { errorHandler.handleException(it) }
        .then(routes(routesContract))

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
            if (runnerStatus == RunnerStatus.READY) {
                runnerStatus = RunnerStatus.EXECUTING
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

                    is DecodingResult.Failure -> errorHandler.handleDecodingError(decodingResult)
                }
                runnerStatus = RunnerStatus.RESTARTING
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
                    RunnerStatus.READY
                )
            )
        } bindContract Method.GET

        fun getStatus() = { _: Request ->
            statusResponseLens.inject(
                StatusResponse(runnerStatus),
                if (runnerStatus == RunnerStatus.READY) Response(OK) else Response(SERVICE_UNAVAILABLE)
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

            is ExecutionResult.Failure -> errorHandler.handleExecutionError(result)
        }
    }

}