package net.developertobi.game.bukkit.arena

import io.papermc.paper.event.player.AsyncChatEvent
import net.developertobi.game.api.arena.ArenaManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * Restricts chat so that when a player in an arena sends a message,
 * only players in the same arena can see it.
 */
class ArenaChatController(
    private val arenaManager: ArenaManager,
) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onAsyncChat(event: AsyncChatEvent) {
        val sender = event.player
        val arenaId = arenaManager.getArenaForPlayer(sender) ?: return

        val arenaPlayers = (arenaManager.getArenaContext(arenaId)?.players ?: emptyList()).toSet()
        val viewers = event.viewers()

        viewers.removeIf { audience ->
            audience is Player && audience !in arenaPlayers
        }
    }
}
