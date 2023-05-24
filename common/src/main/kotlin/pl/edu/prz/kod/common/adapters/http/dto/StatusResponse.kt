package pl.edu.prz.kod.common.adapters.http.dto

import pl.edu.prz.kod.common.domain.RunnerStatus

data class StatusResponse(
    val status: RunnerStatus
)