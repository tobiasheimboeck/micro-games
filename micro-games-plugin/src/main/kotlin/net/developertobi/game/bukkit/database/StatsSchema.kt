package net.developertobi.game.bukkit.database

import org.jetbrains.exposed.sql.Table

object PlayerStatsTable : Table("player_stats") {
    val playerId = varchar("player_id", 36)
    val gameId = varchar("game_id", 64)
    val statId = varchar("stat_id", 64)
    val value = decimal("value", 20, 4)

    override val primaryKey = PrimaryKey(arrayOf(playerId, gameId, statId))
}
