package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.phase.PhaseId
import net.developertobi.game.api.sound.GameSound
import net.kyori.adventure.audience.Audience
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

class EndingPhase(
    private val plugin: Plugin,
) : Phase {

    override val id: PhaseId = PhaseId("ending")
    override val priority: Int = 500

    private var task: BukkitTask? = null
    private var bossBar: ArenaBossBar? = null

    override fun onStart(context: ArenaContext) {
        val durationSeconds = 5
        var remaining = durationSeconds
        val gameName = context.selectedGame?.getProperties()?.name ?: "Game"

        bossBar = context.createBossBar(
            McLibProvider.api.localizationController.line(
                LangKeys.PHASE_ENDING,
                Placeholder.unparsed("game_name", gameName),
                Placeholder.unparsed("remaining", remaining.toString()),
            ),
            1f,
            BossBarColor.RED,
            BossBarOverlay.NOTCHED_10,
        )

        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }

        MicroGamesProvider.api.soundService.play(
            GameSound.GAME_END,
            Audience.audience(context.players),
        )

        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (remaining <= 0) {
                task?.cancel()
                task = null
                bossBar?.removeAll()
                bossBar = null
                context.advanceToNextPhase()
                return@Runnable
            }

            bossBar?.name(
                McLibProvider.api.localizationController.line(
                    LangKeys.PHASE_ENDING,
                    Placeholder.unparsed("game_name", gameName),
                    Placeholder.unparsed("remaining", remaining.toString()),
                ),
            )
            bossBar?.progress(remaining.toFloat() / durationSeconds)
            remaining--
        }, 0L, 20L)
    }

    override fun onStop(context: ArenaContext) {
        task?.cancel()
        task = null
        bossBar?.removeAll()
        bossBar = null
    }

    override fun onPlayerLeft(context: ArenaContext, player: Player) {
        bossBar?.removePlayer(player)
    }
}
