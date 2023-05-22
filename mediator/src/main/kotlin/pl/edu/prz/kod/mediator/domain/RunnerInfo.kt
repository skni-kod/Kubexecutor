package pl.edu.prz.kod.mediator.domain

import pl.edu.prz.kod.common.domain.RunnerStatus

data class RunnerInfo(var status: RunnerStatus, var ipAddress: String?)