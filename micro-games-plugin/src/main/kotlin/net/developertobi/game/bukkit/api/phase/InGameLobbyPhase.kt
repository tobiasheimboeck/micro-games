package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.phase.Phase
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

class InGameLobbyPhase(
    private val plugin: Plugin,
) : Phase {

    override val id: PhaseId = PhaseId("in_game_lobby")
    override val priority: Int = 300

    private var countdownTask: BukkitTask? = null
    private var bossBar: ArenaBossBar? = null

    override fun onStart(context: ArenaContext) {
        val durationSeconds = 60
        var remaining = durationSeconds
        val gameName = context.selectedGame?.getProperties()?.name ?: "Game"

        bossBar = context.createBossBar(
            McLibProvider.api.localizationController.line(
                LangKeys.PHASE_IN_GAME_LOBBY,
                Placeholder.unparsed("game_name", gameName),
                Placeholder.unparsed("remaining", remaining.toString()),
            ),
            1f,
            BossBarColor.YELLOW,
            BossBarOverlay.NOTCHED_10,
        )
        
        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val hasEnoughPlayers = context.players.size >= context.minPlayers

            for (player in context.players) {
                bossBar?.addPlayer(player)
            }

            if (hasEnoughPlayers && remaining <= 0) {
                countdownTask?.cancel()
                countdownTask = null
                bossBar?.removeAll()
                bossBar = null
                context.advanceToNextPhase()
                return@Runnable
            }

            if (hasEnoughPlayers) {
                val audience = Audience.audience(context.players)
                MicroGamesProvider.api.soundService.play(
                    if (remaining == 1) GameSound.COUNTDOWN_FINAL else GameSound.COUNTDOWN_TICK,
                    audience,
                )

                bossBar?.name(
                    McLibProvider.api.localizationController.line(
                        LangKeys.PHASE_IN_GAME_LOBBY,
                        Placeholder.unparsed("game_name", gameName),
                        Placeholder.unparsed("remaining", remaining.toString()),
                    ),
                )
                bossBar?.progress(remaining.toFloat() / durationSeconds)

                for (player in context.players) {
                    player.sendActionBar(
                        McLibProvider.api.localizationController.line(
                            LangKeys.PHASE_IN_GAME_LOBBY_ACTIONBAR,
                            Placeholder.unparsed("game_name", gameName),
                            Placeholder.unparsed("remaining", remaining.toString()),
                        ),
                    )
                }

                remaining--
            } else {
                bossBar?.name(
                    McLibProvider.api.localizationController.line(
                        LangKeys.PHASE_IN_GAME_LOBBY_WAITING,
                        Placeholder.unparsed("current", context.players.size.toString()),
                        Placeholder.unparsed("min", context.minPlayers.toString()),
                    ),
                )
                bossBar?.progress(0f)
            }
        }, 0L, 20L)
    }

    override fun onStop(context: ArenaContext) {
        countdownTask?.cancel()
        countdownTask = null
        bossBar?.removeAll()
        bossBar = null
    }

    override fun onPlayerLeft(context: ArenaContext, player: Player) {
        bossBar?.removePlayer(player)
    }
}
