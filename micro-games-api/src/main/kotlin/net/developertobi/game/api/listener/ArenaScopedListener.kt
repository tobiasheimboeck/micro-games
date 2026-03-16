package net.developertobi.game.api.listener

import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.phase.GameLoopPhase
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEvent

/**
 * Base class for Bukkit listeners that only process events for players in a specific arena.
 *
 * Extend this in [GameLoopPhase] implementations. Register via [ArenaContext.registerListener]
 * in [GameLoopPhase.onStart]; listeners are automatically unregistered when the phase ends.
 *
 * Example:
 * ```
 * class SpleefBlockBreakListener(context: ArenaContext) : ArenaScopedListener(context) {
 *     @EventHandler
 *     fun onBlockBreak(event: BlockBreakEvent) {
 *         onlyIfInArena(event.player) {
 *             // Spleef logic – event.player is in this arena
 *         }
 *     }
 * }
 * // In GameLoopPhase.onStart: context.registerListener(SpleefBlockBreakListener(context))
 * ```
 */
abstract class ArenaScopedListener(
    protected val context: ArenaContext,
) : Listener {

    /**
     * Returns true if the player is in this arena.
     */
    protected fun Player.isInArena(): Boolean = this in context.players

    /**
     * Executes [block] only if [player] is in this arena. Otherwise returns immediately.
     * Use for events that have an associated player (e.g. BlockBreakEvent.player).
     */
    protected inline fun onlyIfInArena(player: Player?, block: () -> Unit) {
        if (player == null || player !in context.players) return
        block()
    }

    /**
     * Executes [block] only if the event's player is in this arena. Otherwise returns immediately.
     * Convenience for [PlayerEvent] subclasses (e.g. PlayerMoveEvent).
     */
    protected inline fun <T : PlayerEvent> T.onlyIfInArena(block: T.() -> Unit) {
        if (player !in context.players) return
        block()
    }
}
