package net.developertobi.game.api.sound

import net.kyori.adventure.audience.Audience

/**
 * Service for playing consistent game sounds.
 * Maps semantic [GameSound] actions to actual sounds so all games share the same audio feedback.
 *
 * Access via [MicroGamesProvider.api].soundService.
 */
interface SoundService {

    /**
     * Play a game sound to the given audience.
     * @param sound The semantic sound action
     * @param audience Players or other audiences to hear the sound (e.g. [Audience.audience] for multiple)
     */
    fun play(sound: GameSound, audience: Audience)
}
