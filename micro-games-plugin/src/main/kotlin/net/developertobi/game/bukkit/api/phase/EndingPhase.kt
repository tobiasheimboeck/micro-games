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
            Component.text("Ending: $gameName – $remaining s").color(NamedTextColor.RED),
            1f,
            BossBarColor.RED,
            BossBarOverlay.NOTCHED_10,
        )
        for (player in context.players) {
            bossBar!!.addPlayer(player)
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (remaining <= 0) {
                task?.cancel()
                task = null
                bossBar?.removeAll()
                bossBar = null
                context.advanceToNextPhase()
                return@Runnable
            }
            bossBar?.name(Component.text("Ending: $gameName – $remaining s").color(NamedTextColor.RED))
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
}
