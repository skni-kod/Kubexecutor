package pl.edu.prz.kod.common

import org.http4k.format.Jackson
import org.http4k.lens.BiDiBodyLens
import pl.edu.prz.kod.common.adapters.http.dto.CodeRequest
import pl.edu.prz.kod.common.adapters.http.dto.CodeResponse
import pl.edu.prz.kod.common.adapters.http.dto.ErrorResponse
import pl.edu.prz.kod.common.adapters.http.dto.StatusResponse

data class Lenses(
    val executeRequestLens: BiDiBodyLens<CodeRequest> = Jackson.autoBody<CodeRequest>().toLens(),
    val executeResponseLens: BiDiBodyLens<CodeResponse> = Jackson.autoBody<CodeResponse>().toLens(),
    val statusResponseLens: BiDiBodyLens<StatusResponse> = Jackson.autoBody<StatusResponse>().toLens(),
    val errorResponseLens: BiDiBodyLens<ErrorResponse> = Jackson.autoBody<ErrorResponse>().toLens()
)
