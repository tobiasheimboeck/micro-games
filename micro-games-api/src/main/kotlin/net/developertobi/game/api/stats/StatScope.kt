package net.developertobi.game.api.stats

import net.developertobi.game.api.arena.ArenaId
import java.util.UUID

sealed class StatScope {
    data class Player(val playerId: UUID) : StatScope()
    data class Arena(val arenaId: ArenaId) : StatScope()
    object Global : StatScope()
}
