package net.developertobi.game.bukkit.database

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val username: String,
    val password: String,
    val poolSize: Int,
    val reconnectIntervalSeconds: Int = 60,
) {
    val jdbcUrl: String
        get() = "jdbc:mariadb://$host:$port/$database"
}
