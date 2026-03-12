package net.developertobi.game.api.game

import net.developertobi.game.api.phase.SubPhase
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
     * Sub-phases for the In-Game phase. Default: empty (arena treats as single implicit sub-phase).
     * Games with multiple states (e.g. Spleef: build → fight) return a list of [SubPhase] implementations.
     */
    fun createPlayingSubPhases(): List<SubPhase> = emptyList()

    /** Optional: Stats that this game tracks. Default: empty. */
    fun getStatDefinitions(): List<StatDefinition> = emptyList()
}
