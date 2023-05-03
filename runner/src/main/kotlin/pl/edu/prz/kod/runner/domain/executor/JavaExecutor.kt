package pl.edu.prz.kod.runner.domain.executor

import pl.edu.prz.kod.runner.domain.ExecutionResult
import pl.edu.prz.kod.runner.domain.Language
import java.io.File
import java.util.regex.Pattern

class JavaExecutor: AbstractExecutor(Language.JAVA) {
    private val srcDirPath = "/app/executor/src"
    private val buildDirPath = "/app/executor/build"
    private val runDirPath = "/app/executor/run"

    override fun execute(code: String): ExecutionResult {
        val srcDir = File(srcDirPath)
        srcDir.mkdirs()
        val runDir = File(runDirPath)
        runDir.mkdirs()

        val classNameMatcher = Pattern.compile("class\\s+([^\\s{]+)[\\s{]").matcher(code)
        val javaClassName = if (classNameMatcher.find()) classNameMatcher.group(1) else "Main"

        val packageNameMatcher = Pattern.compile("^\\s*package\\s+([^;]+);").matcher(code)
        val javaPackageName =
                if (packageNameMatcher.find())
                    packageNameMatcher.group(1)
                            .replace(Regex("\\s"), "") + '.'
                else ""

        val javaFile = srcDir.resolve("$javaClassName.java")
        javaFile.createNewFile()
        javaFile.writeText(code)

        val compilationProcess = runSystemCommand("javac -d $buildDirPath ${javaFile.name}", srcDir)
        if (compilationProcess.timedOut()) {
            return ExecutionResult.Failure.CompilationTimedOutError(timeout)
        }
        if (compilationProcess.exitValue() != 0) {
            return ExecutionResult.Failure.CompilationFailedError(compilationProcess.errorStream.bufferedReader().readText())
        }

        val runProcess = runSystemCommand("java -classpath $buildDirPath $javaPackageName$javaClassName", runDir)
        if (runProcess.timedOut()) {
            return ExecutionResult.Failure.ProcessTimedOutError(timeout)
        }
        return ExecutionResult.Success(
                stdout = runProcess.inputStream.bufferedReader().readText(),
                stdErr = runProcess.errorStream.bufferedReader().readText(),
                exitCode = runProcess.exitValue()
        )
    }
}