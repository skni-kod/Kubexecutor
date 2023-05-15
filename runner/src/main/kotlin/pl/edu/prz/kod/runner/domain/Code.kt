package pl.edu.prz.kod.runner.domain

import pl.edu.prz.kod.common.domain.Language

data class Code(
    val textValue: String,
    val language: Language
)