package pl.edu.prz.kod.domain

enum class Language(val value: String) {
    PYTHON("python"),
    JAVA("java"),
    NODE("node");

    companion object {
        infix fun from(value: String): Language? = Language.values().firstOrNull { it.value == value.lowercase() }
    }
}

