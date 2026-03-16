package net.developertobi.game.api.phase

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.game.MicroGame

/**
 * Game loop phase within the In-Game phase.
 * Each [MicroGame] defines its own via [MicroGame.createGameLoop].
 * Games may have one (e.g. TNT Run) or multiple (e.g. Spleef: build → fight).
 */
interface GameLoopPhase {
    val id: GameLoopPhaseId
    val priority: Int

    fun onStart(context: ArenaContext)
    fun onStop(context: ArenaContext)
}
