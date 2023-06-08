package pl.edu.prz.kod.mediator.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import pl.edu.prz.kod.mediator.application.Configuration
import java.util.*

class DatabaseFactory(private val configuration: Configuration) {

    init {
        initDatabase()
    }

    private fun initDatabase() {
        val hikariConfig = getHikariConfig()
        val dataSource = HikariDataSource(hikariConfig)

        val flyway = Flyway.configure().dataSource(dataSource).load()
        flyway.migrate()
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