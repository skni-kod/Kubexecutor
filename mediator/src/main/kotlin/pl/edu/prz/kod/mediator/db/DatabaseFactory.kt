package pl.edu.prz.kod.mediator.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import pl.edu.prz.kod.common.domain.Language
import pl.edu.prz.kod.mediator.application.Configuration
import java.util.Properties

class DatabaseFactory(private val configuration: Configuration) {

    init {
        initDatabase()
    }

    private fun initDatabase() {
        val hikariConfig = getHikariConfig()
        val dataSource = HikariDataSource(hikariConfig)

        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()

        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Logs)
        }
    }

    private fun getHikariConfig(): HikariConfig {
        val properties = Properties()
        properties.setProperty("jdbcUrl", configuration.jdbcUrl)
        properties.setProperty("dataSource.driverClass", "org.postgresql.Driver")
        properties.setProperty("dataSource.driver", "postgresql")
        properties.setProperty("dataSource.database", configuration.database)
        properties.setProperty("dataSource.user", configuration.databaseUser)
        properties.setProperty("dataSource.password", configuration.databasePassword)
        return HikariConfig(properties)
    }

}