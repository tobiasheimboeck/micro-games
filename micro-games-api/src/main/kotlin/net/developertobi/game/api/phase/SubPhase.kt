package net.developertobi.game.api.phase

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.game.MicroGame

/**
 * Sub-phase within the In-Game phase only.
 * Each [MicroGame] defines its own via [MicroGame.createPlayingSubPhases].
 */
interface SubPhase {
    val id: SubPhaseId
    val priority: Int

    fun onStart(context: ArenaContext)
    fun onStop(context: ArenaContext)
}
