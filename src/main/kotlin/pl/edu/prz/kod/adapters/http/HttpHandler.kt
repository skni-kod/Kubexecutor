package pl.edu.prz.kod.adapters.http

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.adapters.http.dto.*
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*

class HttpHandler {
    private val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
    private val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)

    val app = routes(
        "/execute" bind Method.POST to { request: Request ->
            val codeRequest = codeRequestLens.extract(request)
            val code = codeRequest.decode(base64Decoder)
            val result = executorOrchestrator.execute(code)

            codeResponseLens.inject(result.encode(), Response(Status.OK))
        }
    )
}