package net.developertobi.game.bukkit.api.arena

import net.developertobi.game.api.arena.Arena
import net.developertobi.game.api.arena.ArenaContext
import net.developertobi.game.api.arena.ArenaId
import net.developertobi.game.api.arena.ArenaManager
import net.developertobi.game.api.phase.PhaseProvider
import net.developertobi.game.bukkit.config.ArenaConfig
import net.developertobi.game.bukkit.arena.ArenaChatController
import net.developertobi.game.bukkit.arena.ArenaPlayerBroadcaster
import net.developertobi.game.bukkit.arena.ArenaVisibilityController
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ArenaManagerImpl(
    private val plugin: Plugin,
    private val phaseProvider: PhaseProvider,
    private val arenaConfig: ArenaConfig,
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
        val arena = ArenaImpl(
            id = arenaId,
            plugin = plugin,
            phases = phases,
            visibilityController = visibilityController,
            maxPlayers = arenaConfig.maxPlayers,
            minPlayers = arenaConfig.minPlayers,
            allowSpectators = arenaConfig.allowSpectators,
        )
        arenas[arenaId] = arena
        return arena
    }

    fun removeArena(arenaId: ArenaId) {
        val arena = arenas.remove(arenaId) ?: return
        arena.players.toList().forEach { arena.removePlayer(it) }
        playerToArena.keys.removeAll { playerToArena[it] == arena }
    }

    override fun addPlayerToArena(player: Player, arenaId: ArenaId): Boolean {
        val arena = arenas[arenaId] ?: return false
        if (arena.currentPhase?.id?.value == "ending") return false
        if (arena.players.size >= arena.maxPlayers && !arena.allowSpectators) return false

        playerToArena[player]?.removePlayer(player)
        arena.addPlayer(player)
        playerToArena[player] = arena
        val isSpectator = arena.currentPhase?.id?.value == "in_game" || arena.players.size > arena.maxPlayers
        playerBroadcaster.onPlayerJoined(player, arena, isSpectator)
        return true
    }

    override fun removePlayerFromArena(player: Player): Boolean {
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
