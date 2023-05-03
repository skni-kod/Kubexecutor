package pl.edu.prz.kod.runner.adapters.http.dto

import pl.edu.prz.kod.runner.domain.ExecutorStatus

data class StatusResponse(
    val status: ExecutorStatus
)