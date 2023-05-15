package pl.edu.prz.kod.runner.application

import pl.edu.prz.kod.common.application.BaseEnvironmentVariable

class EnvironmentVariable {
    companion object {
        @JvmStatic
        fun getHttpPort(defaultValue: Int): Int =
            BaseEnvironmentVariable.getIntValue("HTTP_PORT", defaultValue)
        @JvmStatic
        fun getSystemCommandTimeout(defaultValue: Long): Long =
            BaseEnvironmentVariable.getLongValue("SYSTEM_COMMAND_TIMEOUT", defaultValue)
    }
}