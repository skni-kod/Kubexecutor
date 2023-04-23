package pl.edu.prz.kod.adapters.http.dto

import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.Language
import java.util.*

data class CodeRequest(
    val base64Code: String,
    val language: String
) {
    fun decode(b64Decoder: Base64.Decoder): DecodingResult {
        val decodedLanguage =
            Language.from(language) ?: return DecodingResult.Failure.LanguageNotImplementedResult(language)
        return DecodingResult.Successful(
            Code(
                textValue = String(b64Decoder.decode(base64Code)),
                language = decodedLanguage
            )
        )
    }
}

sealed class DecodingResult {
    data class Successful(val code: Code) : DecodingResult()
    sealed class Failure : DecodingResult() {
        data class LanguageNotImplementedResult(val language: String) : Failure()
    }
}