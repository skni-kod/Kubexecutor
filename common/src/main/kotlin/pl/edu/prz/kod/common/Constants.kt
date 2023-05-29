package pl.edu.prz.kod.common

import org.http4k.core.Status

const val STATUS_PATH = "/status"
const val EXECUTE_PATH = "/execute"
val STATUSES_REQUIRING_RESTART = arrayOf(Status.OK, Status.BAD_REQUEST, Status.REQUEST_TIMEOUT)