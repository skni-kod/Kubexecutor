package pl.edu.prz.kod.mediator.ports

import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.mediator.domain.ExecutionContext
import pl.edu.prz.kod.mediator.domain.result.ExecuteRequestResult

interface RunnerManagerPort {
    fun execute(codeRequest: CodeRequest, context: ExecutionContext): ExecuteRequestResult
}