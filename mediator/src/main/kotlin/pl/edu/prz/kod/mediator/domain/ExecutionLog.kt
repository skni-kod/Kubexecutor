package pl.edu.prz.kod.mediator.domain

data class ExecutionLog(
    val email: String,
    val language: String,
    val code: String,
    val stdOut: String,
    val stdErr: String,
    val exitCode: Int
)