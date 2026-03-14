package net.developertobi.game.bukkit.api.arena

import net.developertobi.game.api.arena.Arena
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.SubPhase
import net.developertobi.game.bukkit.api.phase.InGamePhase
import net.developertobi.game.bukkit.arena.ArenaVisibilityController
import org.bukkit.entity.Player

class ArenaImpl(
    override val id: ArenaId,
    private val phases: List<Phase>,
    private val visibilityController: ArenaVisibilityController,
    val maxPlayers: Int,
    val minPlayers: Int,
    val allowSpectators: Boolean,
) : Arena {

    val players: MutableCollection<Player> = mutableListOf()
    val context: ArenaContext = ArenaContextImpl(this)

    var selectedGame: MicroGame? = null

    var currentPhase: Phase? = null
        private set
    var currentSubPhase: SubPhase? = null
        private set

    private var phaseIndex: Int = -1
    private var subPhaseIndex: Int = -1
    private var subPhases: List<SubPhase> = emptyList()

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

    fun advanceToNextPhase() {
        currentSubPhase?.onStop(context)
        currentSubPhase = null
        subPhaseIndex = -1
        subPhases = emptyList()

        currentPhase?.onStop(context)

        phaseIndex++
        if (phaseIndex >= phases.size) {
            phaseIndex = 0
        }

        val nextPhase = phases[phaseIndex]
        currentPhase = nextPhase
        nextPhase.onStart(context)

        if (nextPhase is InGamePhase) {
            subPhases = nextPhase.getSubPhases(context)
            if (subPhases.isNotEmpty()) {
                advanceToNextSubPhase()
            } else {
                advanceToNextPhase()
            }
        }
    }

    fun advanceToNextSubPhase() {
        currentSubPhase?.onStop(context)

        subPhaseIndex++
        if (subPhaseIndex >= subPhases.size) {
            currentSubPhase = null
            subPhaseIndex = -1
            advanceToNextPhase()
            return
        }

        val nextSubPhase = subPhases[subPhaseIndex]
        currentSubPhase = nextSubPhase
        nextSubPhase.onStart(context)
    }
}
