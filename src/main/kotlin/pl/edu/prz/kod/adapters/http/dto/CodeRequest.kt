package pl.edu.prz.kod.adapters.http.dto

import org.http4k.format.Jackson
import pl.edu.prz.kod.domain.Code
import pl.edu.prz.kod.domain.Language
import pl.edu.prz.kod.domain.LanguageNotImplementedError
import java.util.*

val codeRequestLens = Jackson.autoBody<CodeRequest>().toLens()

data class CodeRequest(
    val base64Code: String,
    val language: String
) {
    fun decode(b64Decoder: Base64.Decoder): Code = Code(
        textValue = String(b64Decoder.decode(base64Code)),
        language = Language.from(language) ?: throw LanguageNotImplementedError("Language [${language}] not implemented!")
    )
}