package net.developertobi.game.bukkit.api.sound

import net.developertobi.game.api.sound.GameSound
import net.developertobi.game.api.sound.SoundService
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound as AdventureSound
import org.bukkit.Sound

/**
 * Maps [GameSound] actions to vanilla Minecraft sounds for consistent UX across all games.
 * Each action uses a unique sound for clear audio feedback.
 */
class SoundServiceImpl : SoundService {

    override fun play(sound: GameSound, audience: Audience) {
        val (minecraftSound, volume, pitch) = resolve(sound)
        audience.playSound(
            AdventureSound.sound(minecraftSound, AdventureSound.Source.MASTER, volume, pitch),
        )
    }

    private fun resolve(sound: GameSound): Triple<Sound, Float, Float> = when (sound) {
        GameSound.COUNTDOWN_TICK -> Triple(Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f)
        GameSound.COUNTDOWN_FINAL -> Triple(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        GameSound.GAME_START -> Triple(Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f)
        GameSound.GAME_END -> Triple(Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.6f, 1f)
        GameSound.PHASE_CHANGE -> Triple(Sound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1f)
        GameSound.VOTE_CAST -> Triple(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.4f)
        GameSound.VOTE_WIN -> Triple(Sound.BLOCK_BELL_RESONATE, 0.6f, 1f)
        GameSound.PLAYER_ELIMINATED -> Triple(Sound.ENTITY_PLAYER_DEATH, 0.5f, 1.2f)
        GameSound.PLAYER_WIN -> Triple(Sound.ENTITY_EVOKER_CELEBRATE, 0.7f, 1f)
        GameSound.PLAYER_JOIN -> Triple(Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.2f)
        GameSound.PLAYER_LEAVE -> Triple(Sound.BLOCK_NOTE_BLOCK_BASS, 0.4f, 0.8f)
        GameSound.ACHIEVEMENT -> Triple(Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1f)
        GameSound.CLICK -> Triple(Sound.UI_BUTTON_CLICK, 0.5f, 1f)
        GameSound.ERROR -> Triple(Sound.ENTITY_VILLAGER_NO, 0.6f, 0.9f)
        GameSound.SUCCESS -> Triple(Sound.BLOCK_COMPOSTER_FILL_SUCCESS, 0.6f, 1f)
    }
}
