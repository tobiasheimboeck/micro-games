package net.developertobi.game.api.arena

import org.bukkit.entity.Player

/**
 * Manages arenas on the server.
 * Access via [MicroGamesProvider.api][net.developertobi.game.api.MicroGamesProvider.api].
 */
interface ArenaManager {
    fun createArena(arenaId: ArenaId): Arena
    fun getArenas(): Collection<ArenaId>
    fun getArenaContext(arenaId: ArenaId): ArenaContext?
    fun getArenaForPlayer(player: Player): ArenaId?
}
