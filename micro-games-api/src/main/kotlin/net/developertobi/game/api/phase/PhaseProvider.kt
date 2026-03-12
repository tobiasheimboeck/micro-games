package net.developertobi.game.api.phase

import net.developertobi.game.api.arena.ArenaId

/**
 * Creates the ordered list of phases for an arena.
 * Phases are ordered by [Phase.priority]; the arena controller advances sequentially.
 */
interface PhaseProvider {
    fun createPhases(arenaId: ArenaId): List<Phase>
}
