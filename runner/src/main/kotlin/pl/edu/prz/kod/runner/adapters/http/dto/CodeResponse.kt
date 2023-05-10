package pl.edu.prz.kod.runner.adapters.http.dto

import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse

fun ExecutionResult.Success.encode(): CodeResponse = CodeResponse(
    stdout = stdout,
    stdErr = stdErr,
    exitCode = exitCode
)