package net.developertobi.game.bukkit.api.sound

import net.developertobi.game.api.sound.GameSound
import net.developertobi.game.api.sound.SoundService
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.sound.Sound
import org.bukkit.Sound as BukkitSound

/**
 * Maps [GameSound] actions to vanilla Minecraft sounds for consistent UX across all games.
 */
class SoundServiceImpl : SoundService {

    override fun play(sound: GameSound, audience: Audience) {
        val (bukkitSound, volume, pitch) = resolve(sound)
        audience.playSound(
            Sound.sound(bukkitSound, Sound.Source.MASTER, volume, pitch),
        )
    }

    private fun resolve(sound: GameSound): Triple<BukkitSound, Float, Float> = when (sound) {
        GameSound.COUNTDOWN_TICK -> Triple(BukkitSound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f)
        GameSound.COUNTDOWN_FINAL -> Triple(BukkitSound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
        GameSound.GAME_START -> Triple(BukkitSound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.2f)
        GameSound.GAME_END -> Triple(BukkitSound.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.6f, 1f)
        GameSound.PHASE_CHANGE -> Triple(BukkitSound.BLOCK_NOTE_BLOCK_CHIME, 0.8f, 1f)
        GameSound.VOTE_CAST -> Triple(BukkitSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.4f)
        GameSound.VOTE_WIN -> Triple(BukkitSound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1f)
        GameSound.PLAYER_ELIMINATED -> Triple(BukkitSound.ENTITY_PLAYER_DEATH, 0.5f, 1.2f)
        GameSound.PLAYER_WIN -> Triple(BukkitSound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1f)
        GameSound.PLAYER_JOIN -> Triple(BukkitSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f)
        GameSound.PLAYER_LEAVE -> Triple(BukkitSound.BLOCK_NOTE_BLOCK_BASS, 0.4f, 0.8f)
        GameSound.ACHIEVEMENT -> Triple(BukkitSound.UI_TOAST_CHALLENGE_COMPLETE, 0.6f, 1f)
        GameSound.CLICK -> Triple(BukkitSound.UI_BUTTON_CLICK, 0.5f, 1f)
        GameSound.ERROR -> Triple(BukkitSound.BLOCK_NOTE_BLOCK_BASS, 0.6f, 0.5f)
        GameSound.SUCCESS -> Triple(BukkitSound.BLOCK_COMPOSTER_FILL_SUCCESS, 0.6f, 1f)
    }
}
