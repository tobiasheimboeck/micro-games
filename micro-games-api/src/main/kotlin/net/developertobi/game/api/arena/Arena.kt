package net.developertobi.game.api.arena

/**
 * Represents an arena instance.
 * Created via [ArenaManager.createArena].
 */
interface Arena {
    val id: ArenaId
    fun start()
}
