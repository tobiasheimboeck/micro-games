package net.developertobi.game.bukkit.api.arena

import net.developertobi.game.api.arena.Arena
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.api.phase.PhaseProvider
import net.developertobi.game.bukkit.arena.ArenaChatController
import net.developertobi.game.bukkit.arena.ArenaPlayerBroadcaster
import net.developertobi.game.bukkit.arena.ArenaVisibilityController
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ArenaManagerImpl(
    plugin: Plugin,
    private val phaseProvider: PhaseProvider,
) : ArenaManager {

    private val arenas = mutableMapOf<ArenaId, ArenaImpl>()
    private val playerToArena = mutableMapOf<Player, ArenaImpl>()

    private val visibilityController = ArenaVisibilityController(plugin, this)
    private val playerBroadcaster = ArenaPlayerBroadcaster()

    init {
        plugin.server.pluginManager.registerEvents(ArenaChatController(this), plugin)
    }

    override fun createArena(arenaId: ArenaId): Arena {
        val phases = phaseProvider.createPhases(arenaId)
        val arena = ArenaImpl(arenaId, phases, visibilityController)
        arenas[arenaId] = arena
        return arena
    }

    fun removeArena(arenaId: ArenaId) {
        val arena = arenas.remove(arenaId) ?: return
        arena.players.toList().forEach { arena.removePlayer(it) }
        playerToArena.keys.removeAll { playerToArena[it] == arena }
    }

    fun addPlayerToArena(player: Player, arenaId: ArenaId): Boolean {
        val arena = arenas[arenaId] ?: return false
        if (arena.currentPhase?.id?.value == "ending") return false

        playerToArena[player]?.removePlayer(player)
        arena.addPlayer(player)
        playerToArena[player] = arena
        playerBroadcaster.onPlayerJoined(player, arena, arena.currentPhase?.id?.value == "in_game")
        return true
    }

    fun removePlayerFromArena(player: Player): Boolean {
        val arena = playerToArena.remove(player) ?: return false
        playerBroadcaster.onPlayerLeft(player, arena)
        arena.removePlayer(player)
        return true
    }

    override fun getArenas(): Collection<ArenaId> = arenas.keys.toList()

    override fun getArenaContext(arenaId: ArenaId): ArenaContext? =
        arenas[arenaId]?.context

    override fun getArenaForPlayer(player: Player): ArenaId? =
        playerToArena[player]?.id
}
