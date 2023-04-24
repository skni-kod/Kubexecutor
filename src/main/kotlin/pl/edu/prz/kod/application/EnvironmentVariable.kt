package pl.edu.prz.kod.application

class EnvironmentVariable {
    companion object {
        @JvmStatic
        fun getHttpPort(defaultValue: Int): Int = getIntValue("HTTP_PORT", defaultValue)
        @JvmStatic
        fun getSystemCommandTimeout(defaultValue: Long): Long = getLongValue("SYSTEM_COMMAND_TIMEOUT", defaultValue)

        @JvmStatic
        private fun getIntValue(name: String, defaultValue: Int): Int =
            System.getenv(name)?.toInt() ?: defaultValue
        @JvmStatic
        private fun getLongValue(name: String, defaultValue: Long): Long =
            System.getenv(name)?.toLong() ?: defaultValue
    }
}