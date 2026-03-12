package net.developertobi.game.bukkit.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.configuration.file.FileConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object PluginDatabase {

    private var dataSource: HikariDataSource? = null

    fun connect(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            driverClassName = "org.mariadb.jdbc.Driver"
            username = config.username
            password = config.password
            maximumPoolSize = config.poolSize
            isAutoCommit = false
        }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(datasource = dataSource!!)

        transaction {
            SchemaUtils.create(PlayerStatsTable)
        }
    }

    fun disconnect() {
        dataSource?.close()
        dataSource = null
    }

    fun loadConfig(pluginConfig: FileConfiguration): DatabaseConfig {
        val db = pluginConfig.getConfigurationSection("database") ?: throw IllegalStateException("config.yml: missing 'database' section")
        return DatabaseConfig(
            host = db.getString("host") ?: "localhost",
            port = db.getInt("port", 3306),
            database = db.getString("database") ?: "microgames",
            username = db.getString("username") ?: "microgames",
            password = db.getString("password") ?: "",
            poolSize = db.getInt("pool-size", 5),
            reconnectIntervalSeconds = db.getInt("reconnect-interval", 60),
        )
    }
}
