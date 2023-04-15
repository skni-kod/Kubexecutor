package pl.edu.prz.kod.adapters.http

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KLogger
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import pl.edu.prz.kod.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.adapters.http.dto.encode
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort
import java.util.*


val base64Decoder by inject<Base64.Decoder>(Base64.Decoder::class.java)
val executorOrchestrator by inject<ExecutorOrchestratorPort>(ExecutorOrchestratorPort::class.java)
private val logger by inject<KLogger>(KLogger::class.java)
private val objectMapper by inject<ObjectMapper>(ObjectMapper::class.java)

fun Routing.executor() {
    route("/execute") {
        post {
            val traceId = UUID.randomUUID().toString()
            val codeRequest = call.receive<CodeRequest>()
            logger.infoJson(traceId, "Received call", codeRequest)
            val code = codeRequest.decode(base64Decoder)
            logger.infoJson(traceId, "Decoded call", code)
            val result = executorOrchestrator.execute(code)
            logger.infoJson(traceId, "Executed call", result)

            call.respond(result.encode())
        }
    }
}

fun Logger.infoJson(traceId: String, message: String, param: Any) {
    info("$traceId, $message: ${objectMapper.writeValueAsString(param)}")
}