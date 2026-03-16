package net.developertobi.game.bukkit.api.arena

import net.developertobi.game.api.arena.Arena
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.GameLoopPhase
import net.developertobi.game.bukkit.api.phase.InGamePhase
import net.developertobi.game.bukkit.arena.ArenaVisibilityController
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class ArenaImpl(
    override val id: ArenaId,
    private val plugin: Plugin,
    private val phases: List<Phase>,
    private val visibilityController: ArenaVisibilityController,
    val maxPlayers: Int,
    val minPlayers: Int,
    val allowSpectators: Boolean,
) : Arena {

    private val gameLoopListeners = mutableListOf<Listener>()

    val players: MutableCollection<Player> = mutableListOf()
    val context: ArenaContext = ArenaContextImpl(this)

    var selectedGame: MicroGame? = null

    var currentPhase: Phase? = null
        private set
    var currentGameLoopPhase: GameLoopPhase? = null
        private set

    private var phaseIndex: Int = -1
    private var gameLoopPhaseIndex: Int = -1
    private var gameLoopPhases: List<GameLoopPhase> = emptyList()

    fun addPlayer(player: Player) {
        if (players.add(player)) {
            visibilityController.onPlayerJoinedArena(player, this)
        }
    }

    fun removePlayer(player: Player) {
        if (players.remove(player)) {
            currentPhase?.onPlayerLeft(context, player)
            visibilityController.onPlayerLeftArena(player, this)
        }
    }

    override fun start() {
        advanceToNextPhase()
    }

    fun registerListener(listener: Listener) {
        gameLoopListeners.add(listener)
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    private fun unregisterAllGameLoopListeners() {
        gameLoopListeners.forEach { HandlerList.unregisterAll(it) }
        gameLoopListeners.clear()
    }

    fun advanceToNextPhase() {
        unregisterAllGameLoopListeners()
        currentGameLoopPhase?.onStop(context)
        currentGameLoopPhase = null
        gameLoopPhaseIndex = -1
        gameLoopPhases = emptyList()

        currentPhase?.onStop(context)

        phaseIndex++
        if (phaseIndex >= phases.size) {
            phaseIndex = 0
        }

        val nextPhase = phases[phaseIndex]
        currentPhase = nextPhase
        nextPhase.onStart(context)

        if (nextPhase is InGamePhase) {
            gameLoopPhases = nextPhase.getGameLoopPhases(context)
            if (gameLoopPhases.isNotEmpty()) {
                advanceToNextGameLoopPhase()
            } else {
                advanceToNextPhase()
            }
        }
    }

    fun advanceToNextGameLoopPhase() {
        unregisterAllGameLoopListeners()
        currentGameLoopPhase?.onStop(context)

        gameLoopPhaseIndex++
        if (gameLoopPhaseIndex >= gameLoopPhases.size) {
            currentGameLoopPhase = null
            gameLoopPhaseIndex = -1
            advanceToNextPhase()
            return
        }

        val nextPhase = gameLoopPhases[gameLoopPhaseIndex]
        currentGameLoopPhase = nextPhase
        nextPhase.onStart(context)
    }
}
