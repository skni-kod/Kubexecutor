package pl.edu.prz.kod.application

class EnvironmentVariable {
    companion object {
        @JvmStatic
        fun getHttpPort(defaultValue: Int): Int = getIntValue("HTTP_PORT", defaultValue)

        @JvmStatic
        private fun getIntValue(name: String, defaultValue: Int): Int {
            val stringValue = System.getenv(name)
            return if (stringValue != null) Integer.valueOf(stringValue) else defaultValue
        }
    }
}