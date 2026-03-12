package net.developertobi.game.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.sound.SoundService
import net.developertobi.game.api.stats.StatsService

/**
 * API interface for Micro Games.
 * Extend this interface when adding new functionality.
 *
 * Access via [MicroGamesProvider.api].
 */
interface MicroGamesApi {
    /** All currently loaded Micro Games. */
    val loadedGames: List<MicroGame>

    /** Arena manager. */
    val arenaManager: ArenaManager

    /** Stats service. */
    val statsService: StatsService

    /** Sound service for consistent game sounds. */
    val soundService: SoundService

    /** CoroutineScope for async work. Cancelled when plugin disables. */
    val coroutineScope: CoroutineScope

    /** Dispatcher that runs on the main Minecraft server thread. Use for Bukkit API after awaiting. */
    val minecraftDispatcher: CoroutineDispatcher

    /** Dispatcher for database and I/O work. Use for async DB access. */
    val databaseDispatcher: CoroutineDispatcher
}
