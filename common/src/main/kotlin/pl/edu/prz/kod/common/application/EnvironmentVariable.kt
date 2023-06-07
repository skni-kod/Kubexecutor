package pl.edu.prz.kod.common.application

import pl.edu.prz.kod.common.exception.EnvironmentVariableNotSetException

class EnvironmentVariable {
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

        @JvmStatic
        fun getStringValue(name: String): String =
            System.getenv(name) ?: throw EnvironmentVariableNotSetException(name)
    }
}