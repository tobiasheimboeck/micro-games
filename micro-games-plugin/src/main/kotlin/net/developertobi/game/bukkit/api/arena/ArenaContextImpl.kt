package net.developertobi.game.bukkit.api.arena

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.bossbar.ArenaBossBar
import org.bukkit.event.Listener
import net.developertobi.game.api.bossbar.BossBarColor
import net.developertobi.game.api.bossbar.BossBarOverlay
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.phase.Phase
import net.developertobi.game.api.phase.GameLoopPhase
import net.developertobi.game.bukkit.api.bossbar.ArenaBossBarImpl
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ArenaContextImpl(
    private val arena: ArenaImpl,
) : ArenaContext {

    override val arenaId: ArenaId = arena.id

    override val players: Collection<Player> = arena.players

    override val maxPlayers: Int = arena.maxPlayers

    override val minPlayers: Int = arena.minPlayers

    override val allowSpectators: Boolean = arena.allowSpectators

    override val currentPhase: Phase? = arena.currentPhase

    override val currentGameLoopPhase: GameLoopPhase? = arena.currentGameLoopPhase

    override val selectedGame: MicroGame? = arena.selectedGame

    override fun setSelectedGame(game: MicroGame?) {
        arena.selectedGame = game
    }

    override fun advanceToNextPhase() {
        arena.advanceToNextPhase()
    }

    override fun advanceToNextGameLoopPhase() {
        arena.advanceToNextGameLoopPhase()
    }

    override fun registerListener(listener: Listener) {
        arena.registerListener(listener)
    }

    override fun createBossBar(
        title: Component,
        progress: Float,
        color: BossBarColor,
        overlay: BossBarOverlay,
    ): ArenaBossBar = ArenaBossBarImpl(title, progress, color, overlay)
}
