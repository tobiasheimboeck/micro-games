package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.PhaseProvider
import org.bukkit.plugin.Plugin

class DefaultPhaseProvider(
    private val plugin: Plugin,
) : PhaseProvider {
    override fun createPhases(arenaId: ArenaId): List<Phase> = listOf(
        MapVotingPhase(plugin),
        GameVotingPhase(plugin),
        InGameLobbyPhase(plugin),
        InGamePhase(),
        EndingPhase(plugin),
    )
}
