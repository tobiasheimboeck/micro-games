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

    /** Adds a player to an arena. Returns false if arena not found, in ending phase, or full (without spectators). */
    fun addPlayerToArena(player: Player, arenaId: ArenaId): Boolean

    /** Removes a player from their current arena. Returns false if player was not in any arena. */
    fun removePlayerFromArena(player: Player): Boolean
}
