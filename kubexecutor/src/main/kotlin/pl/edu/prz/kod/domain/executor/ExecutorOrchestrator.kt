package pl.edu.prz.kod.domain.executor

import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort

class ExecutorOrchestrator : ExecutorOrchestratorPort() {
    override fun execute(code: Code): ExecutionResult =
        when (code.language) {
            Language.PYTHON -> PythonExecutor().execute(code.textValue)
            Language.JAVA -> JavaExecutor().execute(code.textValue)
        }

}