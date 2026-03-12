package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.PhaseId
import net.developertobi.game.api.phase.SubPhase
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class InGamePhase : Phase {
    override val id: PhaseId = PhaseId("in_game")
    override val priority: Int = 400

    private var bossBar: ArenaBossBar? = null

    override fun onStart(context: ArenaContext) {
        val gameName = context.selectedGame?.getProperties()?.name ?: "Game"
        bossBar = context.createBossBar(
            Component.text("In-Game: $gameName").color(NamedTextColor.AQUA),
            1f,
            BossBarColor.BLUE,
            BossBarOverlay.PROGRESS,
        )
        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }
    }

    override fun onStop(context: ArenaContext) {
        bossBar?.removeAll()
        bossBar = null
    }

    fun getSubPhases(context: ArenaContext): List<SubPhase> {
        val game = context.selectedGame ?: return emptyList()
        return game.createPlayingSubPhases().sortedBy { it.priority }
    }
}
