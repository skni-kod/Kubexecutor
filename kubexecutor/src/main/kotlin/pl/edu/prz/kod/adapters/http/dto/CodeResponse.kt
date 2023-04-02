package pl.edu.prz.kod.adapters.http.dto

import kotlinx.serialization.Serializable
import pl.edu.prz.kod.domain.ExecutionResult

@Serializable
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