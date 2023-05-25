package pl.edu.prz.kod.mediator.domain

import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Request
import org.http4k.format.Jackson
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse
import pl.edu.prz.kod.common.domain.RunnerStatus
import pl.edu.prz.kod.mediator.adapters.http.RequestAssignedToRunnerEvent
import pl.edu.prz.kod.mediator.adapters.http.RunnerReadyEvent
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.application.Configuration
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RunnerManager(
    private val client: HttpHandler,
    private val configuration: Configuration
) : RunnerManagerPort() {

//    TODO: inject these
    private val executeRequestLens = Jackson.autoBody<CodeRequest>().toLens()
    private val executeResponseLens = Jackson.autoBody<CodeResponse>().toLens()
    private val errorResponseLens = Jackson.autoBody<ErrorResponse>().toLens()

    private val runnersState: ConcurrentHashMap<String, RunnerStatus> = ConcurrentHashMap()

    init {

        (0 until configuration.runnerInstances).forEach {
            runnersState["${configuration.runnerPodName}-${it}"] = RunnerStatus.RESTARTING
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateRunnersState()
            }
        }, 0, configuration.runnerStatusQueryPeriod)
    }

    override fun execute(codeRequest: CodeRequest): ExecuteRequestResult =
        runnersState
            .filterValues { it == RunnerStatus.READY }
            .firstNotNullOfOrNull { it.key }
            ?.let {
                logEvent(RequestAssignedToRunnerEvent(it))
                runnersState[it] = RunnerStatus.EXECUTING
                val requestResult = sendExecuteRequestToRunner(it, codeRequest)
                runnersState[it] = RunnerStatus.RESTARTING
                requestResult
            } ?: ExecuteRequestResult.Failure.NoRunnerAvailable()

    private fun sendExecuteRequestToRunner(runner: String, codeRequest: CodeRequest): ExecuteRequestResult {
        val response = client(
            executeRequestLens(
                codeRequest,
                Request(
                    Method.POST,
                    String.format(configuration.runnerPathFormat, runner) + "/execute"
                )
            )
        )

        return when {
            response.status.successful -> ExecuteRequestResult.Success(executeResponseLens(response))
            response.status == Status.REQUEST_TIMEOUT -> ExecuteRequestResult.Failure.ExecutionTimeout()
            response.status.clientError -> ExecuteRequestResult.Failure.ErrorReplyFromRunner(
                errorResponseLens(response),
                response.status
            )

            else -> ExecuteRequestResult.Failure.NoReplyFromRunner()
        }
    }


    private fun updateRunnersState() {
        runnersState
            .filterValues { it == RunnerStatus.RESTARTING }
            .filter { isRunnerReady(it.key) }
            .forEach {
                runnersState[it.key] = RunnerStatus.READY
                logEvent(RunnerReadyEvent(it.key))
            }
    }

    private fun isRunnerReady(runner: String): Boolean =
        client(
            Request(
                Method.GET,
                String.format(configuration.runnerPathFormat, runner) + "/status"
            )
        ).status.successful

}