package pl.edu.prz.kod.domain.executor

import org.koin.java.KoinJavaComponent.inject
import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.ports.ExecutorOrchestratorPort

class ExecutorOrchestrator : ExecutorOrchestratorPort() {

    private val pythonExecutor by inject<PythonExecutor>(PythonExecutor::class.java)
    private val javaExecutor by inject<JavaExecutor>(JavaExecutor::class.java)
    private val nodeExecutor by inject<NodeExecutor>(NodeExecutor::class.java)

    override fun execute(code: Code): ExecutionResult =
        when (code.language) {
            Language.PYTHON -> pythonExecutor.execute(code.textValue)
            Language.JAVA -> javaExecutor.execute(code.textValue)
            Language.NODE -> nodeExecutor.execute(code.textValue)
        }

}