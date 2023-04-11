package pl.edu.prz.kod.domain

data class ExecutionResult(
    val stdout: String,
    val stdErr: String,
    val exitCode: Int
)