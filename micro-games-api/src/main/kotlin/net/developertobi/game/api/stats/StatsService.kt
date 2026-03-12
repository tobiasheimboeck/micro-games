package net.developertobi.game.api.stats

import kotlinx.coroutines.Deferred
import net.developertobi.game.api.game.GameId
import java.util.UUID

interface StatsService {
    fun record(scope: StatScope.Player, key: StatKey, value: Number)
    fun get(scope: StatScope.Player, key: StatKey): Deferred<Number>
    fun getPlayerStats(playerId: UUID, gameId: GameId): Deferred<Map<StatId, Number>>
    fun getLeaderboard(key: StatKey, limit: Int): Deferred<List<Pair<UUID, Number>>>
}
