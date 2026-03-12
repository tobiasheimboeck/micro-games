package net.developertobi.game.api.phase

import net.developertobi.game.api.arena.ArenaContext

/**
 * Main arena phase. Phases are ordered by [priority]; advancement moves to the next priority.
 * Lifecycle: [onStart] when phase begins, [onStop] when phase ends.
 */
interface Phase {
    val id: PhaseId
    val priority: Int

    fun onStart(context: ArenaContext)
    fun onStop(context: ArenaContext)
}
