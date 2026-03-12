package net.developertobi.game.bukkit.arena

import net.developertobi.game.api.arena.Arena
import net.developertobi.game.api.arena.ArenaManager
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * Ensures players in different arenas cannot see each other.
 * Uses Paper's hideEntity/showEntity for per-player visibility control.
 */
class ArenaVisibilityController(
    private val plugin: Plugin,
    private val arenaManager: ArenaManager,
) {

    fun onPlayerJoinedArena(player: Player, arena: Arena) {
        val playerArenaId = arena.id

        // Hide players from other arenas from this player
        for (otherArenaId in arenaManager.getArenas()) {
            if (otherArenaId == playerArenaId) continue
            for (otherPlayer in arenaManager.getArenaContext(otherArenaId)?.players ?: emptyList()) {
                player.hideEntity(plugin, otherPlayer)
                otherPlayer.hideEntity(plugin, player)
            }
        }
    }

    fun onPlayerLeftArena(player: Player, arena: Arena) {
        val playerArenaId = arena.id

        // Show players from other arenas to this player (and vice versa)
        for (otherArenaId in arenaManager.getArenas()) {
            if (otherArenaId == playerArenaId) continue
            for (otherPlayer in arenaManager.getArenaContext(otherArenaId)?.players ?: emptyList()) {
                player.showEntity(plugin, otherPlayer)
                otherPlayer.showEntity(plugin, player)
            }
        }
    }
}
