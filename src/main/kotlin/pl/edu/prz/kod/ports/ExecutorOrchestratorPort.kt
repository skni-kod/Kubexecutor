package pl.edu.prz.kod.ports

import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.ExecutionResult

abstract class ExecutorOrchestratorPort {

    abstract fun execute(code: Code): ExecutionResult

}