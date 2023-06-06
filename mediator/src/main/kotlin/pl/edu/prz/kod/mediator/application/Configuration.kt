package pl.edu.prz.kod.mediator.application

import pl.edu.prz.kod.common.application.EnvironmentVariable

data class Configuration(
    val httpPort: Int = EnvironmentVariable.getIntValue("HTTP_PORT", 8081),
    val runnerPodName: String = EnvironmentVariable.getStringValue("RUNNER_POD_NAME", "runner"),
    val runnerInstances: Int = EnvironmentVariable.getIntValue("RUNNER_INSTANCES", 1),
    val runnerPathFormat: String = EnvironmentVariable.getStringValue(
        "PATH_FORMAT",
        "http://%s.runner-svc.app.svc.cluster.local:8080"
    ),
    val runnerStatusQueryPeriod: Long = EnvironmentVariable.getLongValue("STATUS_QUERY_PERIOD", 2000),
    val frontendHttpUrl: String = EnvironmentVariable.getStringValue("FRONTEND_HTTP_URL"),
    val backendHttpUrl: String = EnvironmentVariable.getStringValue("BACKEND_HTTP_URL"),
    val oAuthClientId: String = EnvironmentVariable.getStringValue("OAUTH_CLIENT_ID"),
    val oAuthClientSecret: String = EnvironmentVariable.getStringValue("OAUTH_CLIENT_SECRET"),
    val jwtSecret: String = EnvironmentVariable.getStringValue("JWT_SECRET")
)