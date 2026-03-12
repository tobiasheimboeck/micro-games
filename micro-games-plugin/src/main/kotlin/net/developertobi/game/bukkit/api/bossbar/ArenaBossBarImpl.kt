package net.developertobi.game.bukkit.api.bossbar

import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.kyori.adventure.bossbar.BossBar as AdventureBossBar
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

internal class ArenaBossBarImpl(
    title: Component,
    progress: Float,
    color: BossBarColor,
    overlay: BossBarOverlay,
) : ArenaBossBar {

    private val bar: AdventureBossBar = AdventureBossBar.bossBar(
        title,
        progress,
        toAdventureColor(color),
        toAdventureOverlay(overlay),
    )

    private val viewers = mutableSetOf<Player>()

    override fun name(title: Component) {
        bar.name(title)
    }

    override fun progress(progress: Float) {
        bar.progress(progress)
    }

    override fun color(color: BossBarColor) {
        bar.color(toAdventureColor(color))
    }

    override fun overlay(overlay: BossBarOverlay) {
        bar.overlay(toAdventureOverlay(overlay))
    }

    override fun addPlayer(player: Player) {
        if (viewers.add(player)) {
            player.showBossBar(bar)
        }
    }

    override fun removePlayer(player: Player) {
        if (viewers.remove(player)) {
            player.hideBossBar(bar)
        }
    }

    override fun removeAll() {
        for (player in viewers) {
            player.hideBossBar(bar)
        }
        viewers.clear()
    }

    private fun toAdventureColor(color: BossBarColor): AdventureBossBar.Color = when (color) {
        BossBarColor.PINK -> AdventureBossBar.Color.PINK
        BossBarColor.BLUE -> AdventureBossBar.Color.BLUE
        BossBarColor.RED -> AdventureBossBar.Color.RED
        BossBarColor.GREEN -> AdventureBossBar.Color.GREEN
        BossBarColor.YELLOW -> AdventureBossBar.Color.YELLOW
        BossBarColor.PURPLE -> AdventureBossBar.Color.PURPLE
        BossBarColor.WHITE -> AdventureBossBar.Color.WHITE
    }

    private fun toAdventureOverlay(overlay: BossBarOverlay): AdventureBossBar.Overlay = when (overlay) {
        BossBarOverlay.PROGRESS -> AdventureBossBar.Overlay.PROGRESS
        BossBarOverlay.NOTCHED_6 -> AdventureBossBar.Overlay.NOTCHED_6
        BossBarOverlay.NOTCHED_10 -> AdventureBossBar.Overlay.NOTCHED_10
        BossBarOverlay.NOTCHED_12 -> AdventureBossBar.Overlay.NOTCHED_12
        BossBarOverlay.NOTCHED_20 -> AdventureBossBar.Overlay.NOTCHED_20
    }
}
