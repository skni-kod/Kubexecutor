package pl.edu.prz.kod.common.adapters.http.dto

data class CodeRequest(
    val base64Code: String,
    val language: String
)