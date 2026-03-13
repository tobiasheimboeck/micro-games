package net.developertobi.game.bukkit.config

import org.bukkit.configuration.file.FileConfiguration

data class ArenaConfig(
    val count: Int,
    val maxPlayers: Int,
    val minPlayers: Int,
    val allowSpectators: Boolean,
) {
    companion object {
        fun load(pluginConfig: FileConfiguration): ArenaConfig {
            val arenas = pluginConfig.getConfigurationSection("arenas")
            val count = arenas?.getInt("count", 1) ?: 1
            val maxPlayers = arenas?.getInt("max-players", 8) ?: 8
            val minPlayers = arenas?.getInt("min-players", 2) ?: 2
            val allowSpectators = arenas?.getBoolean("allow-spectators", true) ?: true
            return ArenaConfig(
                count = count.coerceAtLeast(1),
                maxPlayers = maxPlayers.coerceIn(1, 100),
                minPlayers = minPlayers.coerceIn(1, maxPlayers),
                allowSpectators = allowSpectators,
            )
        }
    }
}
