package pl.edu.prz.kod.runner

import org.http4k.core.Status

val STATUSES_REQUIRING_RESTART = arrayOf(Status.OK, Status.BAD_REQUEST, Status.REQUEST_TIMEOUT)