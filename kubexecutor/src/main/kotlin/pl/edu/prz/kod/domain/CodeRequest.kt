package pl.edu.prz.kod.domain

import kotlinx.serialization.Serializable
import java.util.*

val b64Decoder = Base64.getDecoder()

@Serializable
data class CodeRequest(
    val base64Code: String,
    val language: String
) {
    fun decode(): Code = Code(
        textValue = String(b64Decoder.decode(base64Code)),
        language = Language.of(language)
    )
}

@Serializable
data class Code(
    val textValue: String,
    val language: Language
)
