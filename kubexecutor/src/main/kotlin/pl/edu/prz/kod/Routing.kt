package pl.edu.prz.kod

import io.ktor.server.routing.*
import io.ktor.server.application.*
import pl.edu.prz.kod.routes.executorRouting

fun Application.configureRouting() {
    routing {
        executorRouting()
    }
}
