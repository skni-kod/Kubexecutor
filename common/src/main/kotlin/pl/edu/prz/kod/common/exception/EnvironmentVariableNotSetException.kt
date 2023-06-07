package pl.edu.prz.kod.common.exception

class EnvironmentVariableNotSetException(env: String): IllegalStateException("Environment variable [${env}] not set.")