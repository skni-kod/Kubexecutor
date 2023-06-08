package pl.edu.prz.kod.mediator.ports

import pl.edu.prz.kod.mediator.domain.ExecutionLog
import pl.edu.prz.kod.mediator.domain.result.InsertResult

interface LogRepositoryPort {
    suspend fun insertLog(log: ExecutionLog): InsertResult
}