package pl.edu.prz.kod.mediator.domain

import kotlinx.coroutines.runBlocking
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Request
import pl.edu.prz.kod.common.EXECUTE_PATH
import pl.edu.prz.kod.common.Lenses
import pl.edu.prz.kod.common.STATUS_PATH
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.domain.RunnerStatus
import pl.edu.prz.kod.mediator.adapters.http.*
import pl.edu.prz.kod.mediator.application.Configuration
import pl.edu.prz.kod.mediator.domain.result.ExecuteRequestResult
import pl.edu.prz.kod.mediator.ports.LogRepositoryPort
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RunnerManager(
    private val client: HttpHandler,
    private val configuration: Configuration,
    private val lenses: Lenses,
    private val logRepository: LogRepositoryPort
) : RunnerManagerPort {
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

    override fun execute(codeRequest: CodeRequest, context: ExecutionContext): ExecuteRequestResult =
        runnersState
            .filterValues { it == RunnerStatus.READY }
            .firstNotNullOfOrNull { it.key }
            ?.let {
                logEvent(RequestAssignedToRunnerEvent(it))
                runnersState[it] = RunnerStatus.EXECUTING
                val requestResult = sendExecuteRequestToRunner(it, codeRequest)
                runnersState[it] = RunnerStatus.RESTARTING
                persistResult(context, codeRequest, requestResult)
                requestResult
            } ?: ExecuteRequestResult.Failure.NoRunnerAvailable()

    private fun sendExecuteRequestToRunner(runner: String, codeRequest: CodeRequest): ExecuteRequestResult {
        val response = client(
            lenses.executeRequestLens(
                codeRequest,
                Request(
                    Method.POST,
                    String.format(configuration.runnerPathFormat, runner) + EXECUTE_PATH
                )
            )
        )

        return when {
            response.status.successful -> ExecuteRequestResult.Success(lenses.executeResponseLens(response))
            response.status == Status.REQUEST_TIMEOUT -> ExecuteRequestResult.Failure.ExecutionTimeout()
            response.status.clientError -> ExecuteRequestResult.Failure.ErrorReplyFromRunner(
                lenses.errorResponseLens(response),
                response.status
            )

            else -> ExecuteRequestResult.Failure.NoReplyFromRunner()
        }
    }


    private fun updateRunnersState() {
        try {
            runnersState
                .filterValues { it == RunnerStatus.RESTARTING }
                .filter { isRunnerReady(it.key) }
                .forEach {
                    runnersState[it.key] = RunnerStatus.READY
                    logEvent(RunnerReadyEvent(it.key))
                }
        } catch (e: Exception) {
            logEvent(ExceptionEvent(e))
        }
    }

    private fun isRunnerReady(runner: String): Boolean {
        val response = client(
            Request(
                Method.GET,
                String.format(configuration.runnerPathFormat, runner) + STATUS_PATH
            )
        )
        return response.status.successful && lenses.statusResponseLens(response).status == RunnerStatus.READY
    }

    private fun persistResult(context: ExecutionContext, request: CodeRequest, result: ExecuteRequestResult) {
        when (result) {
            is ExecuteRequestResult.Success -> runBlocking {
                logRepository.insertLog(
                    ExecutionLog(
                        email = context.email,
                        language = request.language,
                        code = request.base64Code,
                        stdOut = result.codeResponse.stdOut,
                        stdErr = result.codeResponse.stdErr,
                        exitCode = result.codeResponse.exitCode
                    )
                )
                logEvent(LogInsertedEvent(context.email))
            }

            else -> logEvent(FailedToInsertLogEvent(context.email))
        }
    }
}