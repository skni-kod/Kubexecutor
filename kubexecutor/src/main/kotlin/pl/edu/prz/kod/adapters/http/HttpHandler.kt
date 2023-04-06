package pl.edu.prz.kod.adapters.http

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.adapters.http.dto.encode
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*


val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)


fun Routing.executor() {
    route("/execute") {
        post {
            val codeRequest = call.receive<CodeRequest>()
            val code = codeRequest.decode(base64Decoder)

            val result = executorOrchestrator.execute(code)

            call.respond(result.encode())
        }
    }
}