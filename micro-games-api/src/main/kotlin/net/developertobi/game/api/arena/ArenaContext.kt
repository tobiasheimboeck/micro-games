package net.developertobi.game.api.arena

import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.SubPhase
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Context for an arena, providing read-only state and control for phase advancement.
 * Phases and games use this to access arena state and trigger transitions.
 */
interface ArenaContext {
    val arenaId: ArenaId
    val players: Collection<Player>
    val currentPhase: Phase?
    val currentSubPhase: SubPhase?
    val selectedGame: MicroGame?

    fun setSelectedGame(game: MicroGame?)

    fun advanceToNextPhase()
    fun advanceToNextSubPhase()

    /**
     * Create a boss bar for this arena. Use [ArenaBossBar.addPlayer] to show it to players.
     * Call [ArenaBossBar.removeAll] when done.
     */
    fun createBossBar(
        title: Component,
        progress: Float = 1f,
        color: BossBarColor = BossBarColor.WHITE,
        overlay: BossBarOverlay = BossBarOverlay.PROGRESS,
    ): ArenaBossBar
}
