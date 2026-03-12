package net.developertobi.game.bukkit.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import net.developertobi.game.api.MicroGamesApi
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.sound.SoundService
import net.developertobi.game.api.stats.StatsService
import org.bukkit.plugin.java.JavaPlugin

class MicroGamesApiImpl(
    override val loadedGames: List<MicroGame>,
    override val arenaManager: ArenaManager,
    private val plugin: JavaPlugin,
    override val statsService: StatsService,
    override val soundService: SoundService,
    override val coroutineScope: CoroutineScope,
    override val minecraftDispatcher: CoroutineDispatcher,
    override val databaseDispatcher: CoroutineDispatcher,
) : MicroGamesApi
