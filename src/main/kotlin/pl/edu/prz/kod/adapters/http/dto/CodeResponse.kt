package pl.edu.prz.kod.adapters.http.dto

import org.http4k.format.Jackson
import pl.edu.prz.kod.domain.ExecutionResult

val codeResponseLens = Jackson.autoBody<CodeResponse>().toLens()

data class CodeResponse(
    val stdout: String,
    val stdErr: String,
    val exitCode: Int
)

fun ExecutionResult.encode(): CodeResponse = CodeResponse(
    stdout = stdout,
    stdErr = stdErr,
    exitCode = exitCode
)