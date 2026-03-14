package net.developertobi.game.api.phase

import net.developertobi.game.api.arena.ArenaContext
import org.bukkit.entity.Player

/**
 * Main arena phase. Phases are ordered by [priority]; advancement moves to the next priority.
 * Lifecycle: [onStart] when phase begins, [onStop] when phase ends.
 * [onPlayerLeft] when a player leaves the arena while this phase is active.
 */
interface Phase {
    val id: PhaseId
    val priority: Int

    fun onStart(context: ArenaContext)
    fun onStop(context: ArenaContext)

    /** Called when a player leaves the arena. Override to e.g. remove them from phase-specific UI (boss bar). */
    fun onPlayerLeft(context: ArenaContext, player: Player) {}
}
