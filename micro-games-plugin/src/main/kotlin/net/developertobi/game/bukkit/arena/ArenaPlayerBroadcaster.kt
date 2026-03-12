package net.developertobi.game.bukkit.arena

import net.developertobi.game.api.MicroGamesProvider
import net.developertobi.game.api.sound.GameSound
import net.developertobi.game.bukkit.api.arena.ArenaImpl
import net.developertobi.game.bukkit.localization.LangKeys
import net.developertobi.mclib.api.McLibProvider
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

/**
 * Broadcasts player-related messages to all arena players (including bots).
 * Phase-aware: lobby joins vs spectator joins, no joins in Ending phase.
 */
class ArenaPlayerBroadcaster {

    fun onPlayerJoined(player: Player, arena: ArenaImpl, isSpectator: Boolean) {
        val audience = Audience.audience(arena.players)
        val key = if (isSpectator) LangKeys.ARENA_PLAYER_JOIN_SPECTATOR else LangKeys.ARENA_PLAYER_JOIN
        val message = McLibProvider.api.localizationController.line(
            key,
            Placeholder.unparsed("player", player.name),
        )
        audience.sendMessage(message)
        MicroGamesProvider.api.soundService.play(GameSound.PLAYER_JOIN, audience)
    }

    fun onPlayerLeft(player: Player, arena: ArenaImpl) {
        val remaining = arena.players.filter { it != player }
        if (remaining.isEmpty()) return

        val audience = Audience.audience(remaining)
        val message = McLibProvider.api.localizationController.line(
            LangKeys.ARENA_PLAYER_LEAVE,
            Placeholder.unparsed("player", player.name),
        )
        audience.sendMessage(message)
        MicroGamesProvider.api.soundService.play(GameSound.PLAYER_LEAVE, audience)
    }
}
