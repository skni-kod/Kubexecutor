package pl.edu.prz.kod.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import pl.edu.prz.kod.domain.CodeRequest
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.executor.JavaExecutor
import pl.edu.prz.kod.executor.PythonExecutor

fun Route.executorRouting() {
    route("/execute") {
        post {
            val codeRequest = call.receive<CodeRequest>()
            val code = codeRequest.decode()

            val result = when(code.language) {
                Language.PYTHON -> PythonExecutor().execute(code.textValue)
                Language.JAVA -> JavaExecutor().execute(code.textValue)
            }

            call.respond(result)
        }
    }
}