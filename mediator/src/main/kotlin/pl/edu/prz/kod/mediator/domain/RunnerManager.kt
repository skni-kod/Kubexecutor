package pl.edu.prz.kod.mediator.domain

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.http4k.format.Jackson
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse
import pl.edu.prz.kod.common.adapters.http.dto.StatusResponse
import pl.edu.prz.kod.mediator.application.EnvironmentVariable
import pl.edu.prz.kod.common.domain.RunnerStatus
import pl.edu.prz.kod.mediator.adapters.http.RequestAssignedToRunnerEvent
import pl.edu.prz.kod.mediator.adapters.http.RunnerReadyEvent
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.ports.RunnerManagerPort
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RunnerManager : RunnerManagerPort() {
    private val client by inject<OkHttpClient>(OkHttpClient::class.java)

    private val runnersInfo: ConcurrentHashMap<String, RunnerStatus> = ConcurrentHashMap()

    private val runnerInstances = EnvironmentVariable.getRunnerInstances()
    private val runnerName = EnvironmentVariable.getRunnerPodName()
    private val pathFormat = EnvironmentVariable.getPathFormat()
    private val statusQueryPeriod = EnvironmentVariable.getStatusQueryPeriod()

    override fun initialize() {
        (0 until runnerInstances).forEach {
            runnersInfo["$runnerName-${it}"] = RunnerStatus.RESTARTING
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateRunnersState()
            }
        }, 0, statusQueryPeriod)
    }

    override fun execute(codeRequest: CodeRequest): ExecuteRequestResult {
        val freeRunner = runnersInfo
            .filterValues { it == RunnerStatus.READY }
            .firstNotNullOfOrNull { it.key }

        return if (freeRunner == null) {
            ExecuteRequestResult.Failure.NoRunnerAvailable()
        } else {
            logEvent(RequestAssignedToRunnerEvent(freeRunner))
            runnersInfo[freeRunner] = RunnerStatus.EXECUTING
            val requestResult = sendExecuteRequestToRunner(freeRunner, codeRequest)
            runnersInfo[freeRunner] = RunnerStatus.RESTARTING
            requestResult
        }
    }

    private fun sendExecuteRequestToRunner(runner: String, codeRequest: CodeRequest): ExecuteRequestResult {
        val requestBody = Jackson.asFormatString(codeRequest).toRequestBody()
        val request = Request.Builder()
            .url(String.format(pathFormat, runner) + "/execute")
            .post(requestBody)
            .build()
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()
                    ?.let { ExecuteRequestResult.Success(Jackson.asA(it, CodeResponse::class)) }
                    ?: ExecuteRequestResult.Failure.NoReplyFromRunner()
            } else {
                response.body?.string()
                    ?.let {
                        ExecuteRequestResult.Failure.ErrorReplyFromRunner(
                            Jackson.asA(it, ErrorResponse::class),
                            response.code,
                            response.message
                        )
                    }
                    ?: ExecuteRequestResult.Failure.NoReplyFromRunner()
            }
        } catch (_: IOException) {
            ExecuteRequestResult.Failure.NoReplyFromRunner()
        }
    }


    private fun updateRunnersState() {
        runnersInfo
            .filterValues { it == RunnerStatus.RESTARTING }
            .filter { isRunnerReady(it.key) }
            .forEach {
                runnersInfo[it.key] = RunnerStatus.READY
                logEvent(RunnerReadyEvent(it.key))
            }
    }

    private fun isRunnerReady(runner: String): Boolean {
        val request = Request.Builder()
            .url(String.format(pathFormat, runner) + "/status")
            .build()
        return try {
            val response = client.newCall(request).execute()
            return if (response.isSuccessful) {
                response.body?.string()
                    ?.let { Jackson.asA(it, StatusResponse::class).status == RunnerStatus.READY }
                    ?: false
            } else false
        } catch (_: IOException) {
            false
        }
    }
}