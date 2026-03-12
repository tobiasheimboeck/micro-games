package net.developertobi.game.api.bossbar

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Boss bar displayed to arena players.
 *
 * Use [ArenaContext.createBossBar] to create. Mutations (name, progress, etc.) update
 * the bar for all viewers automatically.
 *
 * Call [removeAll] when done to hide from all players and release resources.
 */
interface ArenaBossBar {

    /** Update the displayed title. */
    fun name(title: Component)

    /** Update progress (0.0 to 1.0). */
    fun progress(progress: Float)

    /** Update the bar color. */
    fun color(color: BossBarColor)

    /** Update the overlay style. */
    fun overlay(overlay: BossBarOverlay)

    /** Show this boss bar to the player. */
    fun addPlayer(player: Player)

    /** Hide this boss bar from the player. */
    fun removePlayer(player: Player)

    /** Hide from all players and release resources. Call when the bar is no longer needed. */
    fun removeAll()
}
