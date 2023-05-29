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
    val runnerStatusQueryPeriod: Long = EnvironmentVariable.getLongValue("STATUS_QUERY_PERIOD", 2000)
)