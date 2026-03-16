package net.developertobi.game.api.game

import net.developertobi.game.api.phase.GameLoopPhase
import net.developertobi.game.api.stats.StatDefinition

/**
 * Interface for Micro Games that are loaded via ClassLoader from JAR files.
 *
 * Games are discovered via [java.util.ServiceLoader] using the resource
 * `META-INF/services/net.developertobi.game.api.game.MicroGame`.
 *
 * Each implementation must be annotated with [MicroGameProperties] and provide a parameterless constructor.
 */
interface MicroGame {

    /** Called once when the game is loaded from the JAR. Keep lightweight. */
    fun onLoad() {}

    /** Called when the game is unloaded (e.g. hot unload). Must release static resources if any. */
    fun onUnload() {}

    /**
     * Game loop for the In-Game phase. Default: empty (arena skips In-Game).
     * Games define one (e.g. TNT Run) or multiple (e.g. Spleef: build → fight) [GameLoopPhase] implementations.
     */
    fun createGameLoop(): List<GameLoopPhase> = emptyList()

    /** Optional: Stats that this game tracks. Default: empty. */
    fun getStatDefinitions(): List<StatDefinition> = emptyList()
}
