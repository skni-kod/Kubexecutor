package pl.edu.prz.kod.runner.domain

enum class Language(val value: String) {
    PYTHON("python"),
    JAVA("java"),
    NODEJS("nodejs");

    companion object {
        infix fun from(value: String): Language? = Language.values().firstOrNull { it.value == value.lowercase() }
    }
}

