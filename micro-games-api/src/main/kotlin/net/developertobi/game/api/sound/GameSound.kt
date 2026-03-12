package net.developertobi.game.api.sound

/**
 * Semantic sound actions for Micro Games.
 * Same actions use the same sounds across all games for consistent UX.
 *
 * Use via [SoundService] from [MicroGamesProvider.api].
 */
enum class GameSound {
    /** Countdown tick (e.g. lobby countdown, voting timer). */
    COUNTDOWN_TICK,

    /** Final countdown second (e.g. "1" before start). */
    COUNTDOWN_FINAL,

    /** Game or phase starts. */
    GAME_START,

    /** Game or round ends. */
    GAME_END,

    /** Phase transition (e.g. voting → lobby). */
    PHASE_CHANGE,

    /** Vote cast (e.g. game vote, map vote). */
    VOTE_CAST,

    /** Winning option announced. */
    VOTE_WIN,

    /** Player eliminated from round. */
    PLAYER_ELIMINATED,

    /** Player wins round/game. */
    PLAYER_WIN,

    /** Player joins arena/lobby. */
    PLAYER_JOIN,

    /** Player leaves arena. */
    PLAYER_LEAVE,

    /** Achievement, level up, or milestone. */
    ACHIEVEMENT,

    /** UI click / selection. */
    CLICK,

    /** Error or invalid action. */
    ERROR,

    /** Success or confirmation. */
    SUCCESS,
}
