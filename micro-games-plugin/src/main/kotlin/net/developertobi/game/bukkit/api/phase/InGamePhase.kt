package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.PhaseId
import net.developertobi.game.api.phase.SubPhase
import net.developertobi.game.api.sound.GameSound
import net.kyori.adventure.audience.Audience
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

class InGamePhase : Phase {
    override val id: PhaseId = PhaseId("in_game")
    override val priority: Int = 400

    private var bossBar: ArenaBossBar? = null

    override fun onStart(context: ArenaContext) {
        val gameName = context.selectedGame?.getProperties()?.name ?: "Game"
        bossBar = context.createBossBar(
            McLibProvider.api.localizationController.line(
                LangKeys.PHASE_IN_GAME,
                Placeholder.unparsed("game_name", gameName),
            ),
            1f,
            BossBarColor.BLUE,
            BossBarOverlay.PROGRESS,
        )
        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }

        MicroGamesProvider.api.soundService.play(
            GameSound.GAME_START,
            Audience.audience(context.players),
        )
    }

    override fun onStop(context: ArenaContext) {
        bossBar?.removeAll()
        bossBar = null
    }

    override fun onPlayerLeft(context: ArenaContext, player: Player) {
        bossBar?.removePlayer(player)
    }

    fun getSubPhases(context: ArenaContext): List<SubPhase> {
        val game = context.selectedGame ?: return emptyList()
        return game.createPlayingSubPhases().sortedBy { it.priority }
    }
}
