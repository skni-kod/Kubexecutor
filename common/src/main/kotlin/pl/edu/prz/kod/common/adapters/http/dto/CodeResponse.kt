package pl.edu.prz.kod.common.adapters.http.dto

data class CodeResponse(
    val stdOut: String,
    val stdErr: String,
    val exitCode: Int
)