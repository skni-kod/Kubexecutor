package pl.edu.prz.kod.runner.ports

import pl.edu.prz.kod.runner.domain.Code
import pl.edu.prz.kod.runner.domain.ExecutionResult

abstract class ExecutorOrchestratorPort {

    abstract fun execute(code: Code): ExecutionResult

}