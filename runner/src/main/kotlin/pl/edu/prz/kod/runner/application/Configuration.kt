package pl.edu.prz.kod.runner.application

import pl.edu.prz.kod.common.application.EnvironmentVariable

data class Configuration(
    val httpPort: Int = EnvironmentVariable.getIntValue("HTTP_PORT", 8080),
    val systemCommandTimeout: Long = EnvironmentVariable.getLongValue("SYSTEM_COMMAND_TIMEOUT", 5000)
)