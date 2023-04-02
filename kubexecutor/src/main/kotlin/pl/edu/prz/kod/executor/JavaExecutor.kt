package pl.edu.prz.kod.executor

import pl.edu.prz.kod.domain.ExecutionResult
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.error.CompilationFailedError
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
        var javaClassName = "Main"
        if (classNameMatcher.find()) {
            javaClassName = classNameMatcher.group(1)
        }

        val javaFile = srcDir.resolve("$javaClassName.java")
        javaFile.createNewFile()
        javaFile.writeText(code)

        val compilationProcess = runSystemCommand("javac -d $buildDirPath ${javaFile.name}", srcDir)
        if (compilationProcess.exitValue() != 0) {
            throw CompilationFailedError(compilationProcess.errorStream.bufferedReader().readText())
        }

        val runProcess = runSystemCommand("java -classpath $buildDirPath $javaClassName", runDir)
        return ExecutionResult(
                stdout = runProcess.inputStream.bufferedReader().readText(),
                stdErr = runProcess.errorStream.bufferedReader().readText(),
                exitCode = runProcess.exitValue()
        )
    }
}