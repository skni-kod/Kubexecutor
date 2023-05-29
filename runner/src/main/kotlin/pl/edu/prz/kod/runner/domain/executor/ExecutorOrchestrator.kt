package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.common.domain.Language
import pl.edu.prz.kod.runner.domain.Code
import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.runner.ports.ExecutorOrchestratorPort

class ExecutorOrchestrator(
    private val pythonExecutor: PythonExecutor,
    private val javaExecutor: JavaExecutor,
    private val nodeJSExecutor: NodeJSExecutor
) : ExecutorOrchestratorPort() {
    override fun execute(code: Code): ExecutionResult =
        when (code.language) {
            Language.PYTHON -> pythonExecutor.execute(code.textValue)
            Language.JAVA -> javaExecutor.execute(code.textValue)
            Language.NODEJS -> nodeJSExecutor.execute(code.textValue)
        }

}