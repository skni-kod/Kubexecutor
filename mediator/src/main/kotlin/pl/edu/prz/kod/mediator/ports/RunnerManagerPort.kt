package pl.edu.prz.kod.mediator.ports

import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.mediator.domain.ExecuteRequestResult

abstract class RunnerManagerPort {
    abstract fun execute(codeRequest: CodeRequest): ExecuteRequestResult
}