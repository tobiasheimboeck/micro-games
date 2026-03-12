package net.developertobi.game.bukkit.api.phase

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.PhaseId
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
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
            Component.text("$gameName startet in $remaining s").color(NamedTextColor.GOLD),
            1f,
            BossBarColor.YELLOW,
            BossBarOverlay.NOTCHED_10,
        )
        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (remaining <= 0) {
                countdownTask?.cancel()
                countdownTask = null
                bossBar?.removeAll()
                bossBar = null
                context.advanceToNextPhase()
                return@Runnable
            }

            bossBar?.name(Component.text("$gameName startet in $remaining s").color(NamedTextColor.GOLD))
            bossBar?.progress(remaining.toFloat() / durationSeconds)

            for (player in context.players) {
                player.sendActionBar(
                    Component.text("$gameName startet in ")
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("$remaining").color(NamedTextColor.GOLD))
                        .append(Component.text(" Sekunden...").color(NamedTextColor.GRAY)),
                )
            }

            remaining--
        }, 0L, 20L)
    }

    override fun onStop(context: ArenaContext) {
        countdownTask?.cancel()
        countdownTask = null
        bossBar?.removeAll()
        bossBar = null
    }
}
