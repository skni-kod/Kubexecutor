package pl.edu.prz.kod.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class Language {
    @Serializable
    object Python : Language()

    companion object {
        fun of(language: String): Language = when (language) {
            "Python" -> Python
            else -> throw NotImplementedError()
        }
    }
}
