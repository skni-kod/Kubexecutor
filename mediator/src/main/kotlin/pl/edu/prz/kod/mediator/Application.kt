package pl.edu.prz.kod.mediator

import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

enum class RunnerState {
    READY, WORKING, RESTARTING
}

val state: ConcurrentHashMap<String, RunnerState> = ConcurrentHashMap()
val client: HttpHandler = OkHttp()

val httpPort: Int = System.getenv("HTTP_PORT")?.toInt() ?: 8081
val runnerName: String = System.getenv("RUNNER_POD_NAME") ?: "runner"
val runnerInstances: Int = System.getenv("RUNNER_INSTANCES")?.toInt() ?: 2
val pathFormat: String = System.getenv("PATH_FORMAT") ?: "http://%s.runner-svc.app.svc.cluster.local:8080"
val statusQueryPeriod: Long = System.getenv("STATUS_QUERY_PERIOD")?.toLong() ?: 2000
fun main() {

    (0 until runnerInstances).forEach {
        state["${runnerName}-${it}"] = RunnerState.READY
    }

    Timer().scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            state
                .filterValues { it == RunnerState.RESTARTING }
                .map { Pair(it.key, client(Request(Method.GET, String.format(pathFormat, it.key) + "/status"))) }
                .filter { it.second.status == Status.OK }
                .forEach { state[it.first] = RunnerState.READY }
        }
    }, 0, statusQueryPeriod)

    val app: HttpHandler = { request: Request ->
        val freeRunner = state
            .filterValues { it == RunnerState.READY }
            .firstNotNullOfOrNull { it.key }

        if (freeRunner == null)
            Response(Status.SERVICE_UNAVAILABLE)
        else {
            state[freeRunner] = RunnerState.WORKING
            val response = client(Request(Method.POST, String.format(pathFormat, freeRunner) + "/execute").body(request.body))
            state[freeRunner] = RunnerState.RESTARTING
            response
        }
    }

    app.asServer(Netty(port = httpPort)).start()
}