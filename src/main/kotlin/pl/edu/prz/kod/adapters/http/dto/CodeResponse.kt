package pl.edu.prz.kod.adapters.http.dto

import pl.edu.prz.kod.domain.ExecutionResult

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