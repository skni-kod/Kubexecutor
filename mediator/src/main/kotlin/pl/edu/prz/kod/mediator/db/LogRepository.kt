package pl.edu.prz.kod.mediator.db

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import pl.edu.prz.kod.mediator.adapters.http.ExceptionEvent
import pl.edu.prz.kod.mediator.adapters.http.logEvent
import pl.edu.prz.kod.mediator.domain.ExecutionLog
import pl.edu.prz.kod.mediator.domain.result.InsertResult
import pl.edu.prz.kod.mediator.ports.LogRepositoryPort

class LogRepository : LogRepositoryPort {
    override suspend fun insertLog(log: ExecutionLog): InsertResult {
        return try {
            transaction {
                Logs.insert {
                    it[email] = log.email
                    it[language] = log.language
                    it[code] = ExposedBlob(log.code.toByteArray())
                    it[stdOut] = ExposedBlob(log.stdOut.toByteArray())
                    it[stdErr] = ExposedBlob(log.stdErr.toByteArray())
                    it[exitCode] = log.exitCode
                }
            }
            InsertResult.Success
        } catch (e: ExposedSQLException) {
            logEvent(ExceptionEvent(e))
            InsertResult.Failure
        }
    }
}