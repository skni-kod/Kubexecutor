package pl.edu.prz.kod.common.application

class BaseEnvironmentVariable {
    companion object {
        @JvmStatic
        fun getIntValue(name: String, defaultValue: Int): Int =
            System.getenv(name)?.toInt() ?: defaultValue

        @JvmStatic
        fun getLongValue(name: String, defaultValue: Long): Long =
            System.getenv(name)?.toLong() ?: defaultValue

        @JvmStatic
        fun getStringValue(name: String, defaultValue: String): String =
            System.getenv(name) ?: defaultValue
    }
}