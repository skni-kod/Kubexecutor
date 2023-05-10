package pl.edu.prz.kod.runner.adapters.http.dto

import pl.edu.prz.kod.runner.domain.Code
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.domain.Language
import java.util.*

fun CodeRequest.decode(b64Decoder: Base64.Decoder): DecodingResult {
    val decodedLanguage =
        Language.from(language) ?: return DecodingResult.Failure.LanguageNotImplementedResult(language)
    return DecodingResult.Successful(
        Code(
            textValue = String(b64Decoder.decode(base64Code)),
            language = decodedLanguage
        )
    )
}

sealed class DecodingResult {
    data class Successful(val code: Code) : DecodingResult()
    sealed class Failure : DecodingResult() {
        data class LanguageNotImplementedResult(val language: String) : Failure()
    }
}