package pl.edu.prz.kod.runner.adapters.http

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
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.runner.adapters.http.dto.*
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort
import pl.edu.prz.kod.common.adapters.http.dto.StatusResponse
import pl.edu.prz.kod.common.domain.RunnerStatus
import pl.edu.prz.kod.runner.adapters.http.filter.RunnerStatusFilter
import pl.edu.prz.kod.runner.domain.*
import java.util.*

class RunnerHttpHandler(
    private val base64Decoder: Base64.Decoder,
    private val errorHandler: ErrorHandler,
    private val executorOrchestrator: ExecutorOrchestratorPort,
    private val lenses: Lenses
) {
    private var runnerStatus = RunnerStatus.READY

    private val routesContract = contract {
        renderer = OpenApi2(ApiInfo("Kubexecutor Runner API", "v1.0"), Jackson)
        descriptionPath = "/openapi.json"
        routes += executeRoute()
        routes += statusRoute()
    }

    private val statusHandler = RunnerStatusFilter { runnerStatus }
        .then(routes(routesContract))

    private val exceptionCatchingHandler: RoutingHttpHandler = ServerFilters
        .CatchAll { errorHandler.handleException(it) }
        .then(statusHandler)

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
        val spec = EXECUTE_PATH meta {
            summary = "Executes code request"
            receiving(
                lenses.executeRequestLens to CodeRequest(
                    base64Code = "cHJpbnQoImhlbGxvLCB3b3JsZCEiKQ==",
                    language = "python"
                )
            )
            returning(
                OK,
                lenses.executeResponseLens to CodeResponse(
                    stdout = "hello,world!",
                    stdErr = "",
                    exitCode = 0
                )
            )
        } bindContract Method.POST

        fun execute() = { request: Request ->
            runnerStatus = RunnerStatus.EXECUTING
            val codeRequest = lenses.executeRequestLens.extract(request)
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
            runnerStatus =
                if (STATUSES_REQUIRING_RESTART.contains(result.status)) RunnerStatus.RESTARTING
                else RunnerStatus.READY
            result
        }

        return spec to ::execute
    }

    private fun statusRoute(): ContractRoute {
        val spec = STATUS_PATH meta {
            summary = "Gets current status of executor"
            returning(
                OK,
                lenses.statusResponseLens to StatusResponse(RunnerStatus.READY)
            )
        } bindContract Method.GET

        fun getStatus() = { _: Request ->
            lenses.statusResponseLens.inject(StatusResponse(runnerStatus), Response(OK))
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
                lenses.executeResponseLens.inject(result.encode(), Response(OK))
            }

            is ExecutionResult.Failure -> errorHandler.handleExecutionError(result)
        }
    }

}