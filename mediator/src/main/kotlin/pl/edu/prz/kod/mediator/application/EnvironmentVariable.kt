package pl.edu.prz.kod.mediator.application

import pl.edu.prz.kod.common.application.BaseEnvironmentVariable

class EnvironmentVariable {
    companion object {
        @JvmStatic
        fun getHttpPort() =
            BaseEnvironmentVariable.getIntValue("HTTP_PORT", 8081)

        @JvmStatic
        fun getRunnerPodName() =
            BaseEnvironmentVariable.getStringValue("RUNNER_POD_NAME", "runner")

        @JvmStatic
        fun getRunnerInstances() =
            BaseEnvironmentVariable.getIntValue("RUNNER_INSTANCES", 2)

        @JvmStatic
        fun getPathFormat() =
            BaseEnvironmentVariable.getStringValue("PATH_FORMAT", "http://%s.runner-svc.app.svc.cluster.local:8080")

        @JvmStatic
        fun getStatusQueryPeriod() =
            BaseEnvironmentVariable.getLongValue("STATUS_QUERY_PERIOD", 2000)
    }
}