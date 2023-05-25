package pl.edu.prz.kod.mediator.application

import pl.edu.prz.kod.common.application.BaseEnvironmentVariable

data class Configuration(
    val httpPort: Int = BaseEnvironmentVariable.getIntValue("HTTP_PORT", 8081),
    val runnerPodName: String = BaseEnvironmentVariable.getStringValue("RUNNER_POD_NAME", "runner"),
    val runnerInstances: Int = BaseEnvironmentVariable.getIntValue("RUNNER_INSTANCES", 1),
    val runnerPathFormat: String = BaseEnvironmentVariable.getStringValue(
        "PATH_FORMAT",
        "http://%s.runner-svc.app.svc.cluster.local:8080"
    ),
    val runnerStatusQueryPeriod: Long = BaseEnvironmentVariable.getLongValue("STATUS_QUERY_PERIOD", 2000)
)