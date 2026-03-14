package net.developertobi.game.bukkit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.bukkit.api.MicroGamesApiImpl
import net.developertobi.game.bukkit.api.arena.ArenaManagerImpl
import net.developertobi.game.bukkit.api.phase.DefaultPhaseProvider
import net.developertobi.game.bukkit.api.sound.SoundServiceImpl
import net.developertobi.game.bukkit.api.stats.BufferedStatsService
import net.developertobi.game.bukkit.api.stats.DelegatingStatsService
import net.developertobi.game.bukkit.api.stats.StatsServiceImpl
import net.developertobi.game.bukkit.config.ArenaConfig
import net.developertobi.game.bukkit.database.DatabaseConfig
import net.developertobi.game.bukkit.database.PluginDatabase
import net.developertobi.game.bukkit.command.ArenaCommand
import net.developertobi.game.bukkit.loader.GameLoader
import net.developertobi.mclib.api.McLibProvider
import net.developertobi.mclib.api.plugin.McLibPluginBootstrap
import org.bukkit.plugin.java.JavaPlugin

class MicroGamesPlugin : JavaPlugin() {

    private lateinit var gameLoader: GameLoader
    private lateinit var arenaManager: ArenaManager
    private lateinit var coroutineScope: CoroutineScope
    private var reconnectJob: Job? = null

    override fun onEnable() {
        McLibPluginBootstrap.setup(this) {
            localizationEnabled = true
            onReady = { initPlugin() }
        }
    }

    private fun initPlugin() {
        saveDefaultConfig()
        reloadConfig()

        gameLoader = GameLoader(this)
        gameLoader.loadAll()

        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val minecraftDispatcher = MinecraftDispatcher(this)
        val databaseDispatcher = Dispatchers.IO

        val dbConfig = PluginDatabase.loadConfig(config)
        val statsService = try {
            PluginDatabase.connect(dbConfig)
            StatsServiceImpl(this, gameLoader.loadedGames, coroutineScope)
        } catch (e: Exception) {
            logger.warning("Database connection failed: ${e.message}. Stats buffered until DB is available.")
            BufferedStatsService(gameLoader.loadedGames)
        }

        val delegatingStatsService = DelegatingStatsService(statsService)
        if (statsService is BufferedStatsService && dbConfig.reconnectIntervalSeconds > 0) {
            startReconnectTask(dbConfig, statsService, delegatingStatsService)
        }

        val arenaConfig = ArenaConfig.load(config)
        arenaManager = ArenaManagerImpl(this, DefaultPhaseProvider(this), arenaConfig)

        repeat(arenaConfig.count) { index ->
            arenaManager.createArena(ArenaId("arena-${index + 1}")).start()
        }

        MicroGamesProvider.api = MicroGamesApiImpl(
            loadedGames = gameLoader.loadedGames,
            arenaManager = arenaManager,
            plugin = this,
            statsService = delegatingStatsService,
            soundService = SoundServiceImpl(),
            coroutineScope = coroutineScope,
            minecraftDispatcher = minecraftDispatcher,
            databaseDispatcher = databaseDispatcher,
        )

        McLibProvider.api.commandController.registerCommand(ArenaCommand(), this)
    }

    private fun startReconnectTask(
        dbConfig: DatabaseConfig,
        buffered: BufferedStatsService,
        delegating: DelegatingStatsService,
    ) {
        if (dbConfig.reconnectIntervalSeconds <= 0) return

        reconnectJob = coroutineScope.launch {
            while (isActive) {
                delay(dbConfig.reconnectIntervalSeconds.toLong() * 1000L)
                if (!isActive) return@launch
                try {
                    PluginDatabase.connect(dbConfig)
                    val statsServiceImpl = StatsServiceImpl(this@MicroGamesPlugin, gameLoader.loadedGames, coroutineScope)
                    withContext(Dispatchers.IO) {
                        buffered.flush(statsServiceImpl)
                    }
                    delegating.setDelegate(statsServiceImpl)
                    reconnectJob?.cancel()
                    reconnectJob = null
                    logger.info("Database connected. Buffered stats flushed.")
                    return@launch
                } catch (e: Exception) {
                    logger.fine("Database reconnect failed: ${e.message}")
                }
            }
        }
    }

    override fun onDisable() {
        val api = MicroGamesProvider.api
        if (api.statsService is DelegatingStatsService) {
            val delegate = (api.statsService as DelegatingStatsService).delegate
            if (delegate is BufferedStatsService && delegate.hasBufferedData()) {
                logger.warning("Database was offline. Buffered stats will be lost.")
            }
        }
        reconnectJob?.cancel()
        reconnectJob = null
        coroutineScope.cancel()
        runBlocking {
            withTimeout(5000) {
                coroutineScope.coroutineContext[Job]?.join()
            }
        }
        PluginDatabase.disconnect()
        gameLoader.unloadAll()
    }
}
