package pl.edu.prz.kod.domain

import kotlinx.serialization.Serializable
import pl.edu.prz.kod.LanguageNotImplementedError
import pl.edu.prz.kod.b64Decoder

@Serializable
data class CodeRequest(
    val base64Code: String,
    val language: String
) {
    fun decode(): Code = Code(
        textValue = String(b64Decoder.decode(base64Code)),
        language = Language.from(language) ?: throw LanguageNotImplementedError("Language [${language}] not implemented!")
    )
}

@Serializable
data class Code(
    val textValue: String,
    val language: Language
)
