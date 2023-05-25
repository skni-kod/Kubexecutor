package pl.edu.prz.kod.runner.application

import pl.edu.prz.kod.common.application.BaseEnvironmentVariable

data class Configuration(
    val httpPort: Int = BaseEnvironmentVariable.getIntValue("HTTP_PORT", 8081),
    val systemCommandTimeout: Long = BaseEnvironmentVariable.getLongValue("SYSTEM_COMMAND_TIMEOUT", 5000)
)