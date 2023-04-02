package pl.edu.prz.kod.adapters.http

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pl.edu.prz.kod.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.adapters.http.dto.encode
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*

class HttpHandler(application: Application, executorOrchestrator: ExecutorOrchestratorPort) {

    private val base64Decoder = Base64.getDecoder()

    init {
        with(application) {
            routing {
                route("/execute") {
                    post {
                        val codeRequest = call.receive<CodeRequest>()
                        val code = codeRequest.decode(base64Decoder)

                        val result = executorOrchestrator.execute(code)

                        call.respond(result.encode())
                    }
                }
            }
        }
    }

}