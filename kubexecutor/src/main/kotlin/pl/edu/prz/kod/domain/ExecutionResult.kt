package pl.edu.prz.kod.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExecutionResult(
    val stdout: String,
    val stdErr: String,
    val exitCode: Int
)
