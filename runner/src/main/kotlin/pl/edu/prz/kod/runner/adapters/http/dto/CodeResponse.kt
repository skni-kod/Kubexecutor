package pl.edu.prz.kod.runner.adapters.http.dto

import pl.edu.prz.kod.runner.domain.ExecutionResult

data class CodeResponse(
    val stdout: String,
    val stdErr: String,
    val exitCode: Int
)

fun ExecutionResult.Success.encode(): CodeResponse = CodeResponse(
    stdout = stdout,
    stdErr = stdErr,
    exitCode = exitCode
)