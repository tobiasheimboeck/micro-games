package net.developertobi.game.bukkit.api.stats

import kotlinx.coroutines.Deferred
import net.developertobi.game.api.game.GameId
import net.developertobi.game.api.stats.StatId
import net.developertobi.game.api.stats.StatKey
import net.developertobi.game.api.stats.StatScope
import net.developertobi.game.api.stats.StatsService
import java.util.UUID

/**
 * StatsService that delegates to a mutable underlying implementation.
 * Used to swap from BufferedStatsService to StatsServiceImpl when the database connects.
 */
class DelegatingStatsService(
    initial: StatsService,
) : StatsService {

    @Volatile
    var delegate: StatsService = initial
        private set

    fun setDelegate(newDelegate: StatsService) {
        delegate = newDelegate
    }

    override fun record(scope: StatScope.Player, key: StatKey, value: Number) {
        delegate.record(scope, key, value)
    }

    override fun get(scope: StatScope.Player, key: StatKey): Deferred<Number> =
        delegate.get(scope, key)

    override fun getPlayerStats(playerId: UUID, gameId: GameId): Deferred<Map<StatId, Number>> =
        delegate.getPlayerStats(playerId, gameId)

    override fun getLeaderboard(key: StatKey, limit: Int): Deferred<List<Pair<UUID, Number>>> =
        delegate.getLeaderboard(key, limit)
}
