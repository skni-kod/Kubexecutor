package pl.edu.prz.kod.domain

enum class Language(val value: String) {
    PYTHON("python");

    companion object {
        infix fun from(value: String): Language? = Language.values().firstOrNull { it.value == value.lowercase() }
    }
}

