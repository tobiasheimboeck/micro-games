package net.developertobi.game.api.arena

import net.developertobi.game.api.bossbar.ArenaBossBar
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.GameLoopPhase
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.Listener

/**
 * Context for an arena, providing read-only state and control for phase advancement.
 * Phases and games use this to access arena state and trigger transitions.
 */
interface ArenaContext {

    val arenaId: ArenaId
    val players: Collection<Player>
    val maxPlayers: Int
    val minPlayers: Int
    val allowSpectators: Boolean
    val currentPhase: Phase?
    val currentGameLoopPhase: GameLoopPhase?
    val selectedGame: MicroGame?

    fun setSelectedGame(game: MicroGame?)

    fun advanceToNextPhase()
    fun advanceToNextGameLoopPhase()

    /**
     * Registers a listener for the current game loop phase. It will be automatically unregistered
     * when the phase ends (on next phase advance). Use for [ArenaScopedListener] instances.
     */
    fun registerListener(listener: Listener)

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
