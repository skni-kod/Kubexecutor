package pl.edu.prz.kod.mediator.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

object Logs: IntIdTable() {
    val email: Column<String> = varchar("EMAIL", 255)
    val language: Column<String> = varchar("LANGUAGE", 64)
    val code: Column<ExposedBlob> = blob("CODE")
    val stdOut: Column<ExposedBlob> = blob("STDOUT")
    val stdErr: Column<ExposedBlob> = blob("STDERR")
    val exitCode: Column<Int> = integer("EXITCODE")
}