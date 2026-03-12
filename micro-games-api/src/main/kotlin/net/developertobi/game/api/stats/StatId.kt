package net.developertobi.game.api.stats

import net.developertobi.game.api.game.GameId

@JvmInline
value class StatId(val value: String)

data class StatKey(
    val gameId: GameId,
    val statId: StatId
) {
    val fullKey: String get() = "${gameId.value}:${statId.value}"
}
