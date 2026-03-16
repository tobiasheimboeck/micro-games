package net.developertobi.game.bukkit.api.stats

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import net.developertobi.game.api.game.GameId
import net.developertobi.game.api.game.MicroGame
import net.developertobi.game.api.game.getProperties
import net.developertobi.game.api.stats.StatAggregation
import net.developertobi.game.api.stats.StatDefinition
import net.developertobi.game.api.stats.StatId
import net.developertobi.game.api.stats.StatKey
import net.developertobi.game.api.stats.StatScope
import net.developertobi.game.api.stats.StatType
import net.developertobi.game.api.stats.StatsService
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private data class BufferedStatKey(
    val playerId: UUID,
    val gameId: String,
    val statId: String,
)

/**
 * StatsService that buffers records in memory when the database is offline.
 * Call [flush] when the database becomes available to push buffered data.
 */
class BufferedStatsService(
    private val loadedGames: List<MicroGame>,
) : StatsService {

    private val buffer = ConcurrentHashMap<BufferedStatKey, Double>()

    override fun record(scope: StatScope.Player, key: StatKey, value: Number) {
        val definition = getDefinition(key) ?: return
        val bufferedKey = BufferedStatKey(scope.playerId, key.gameId.value, key.statId.value)

        buffer.compute(bufferedKey) { _, currentVal ->
            val current = currentVal ?: definition.defaultValue.toDouble()
            when (definition.aggregation) {
                StatAggregation.SUM -> current + value.toDouble()
                StatAggregation.MAX -> maxOf(current, value.toDouble())
                StatAggregation.MIN -> minOf(current, value.toDouble())
                StatAggregation.LAST -> value.toDouble()
            }
        }
    }

    override fun get(scope: StatScope.Player, key: StatKey): Deferred<Number> {
        val definition = getDefinition(key) ?: return CompletableDeferred(0)
        val bufferedKey = BufferedStatKey(scope.playerId, key.gameId.value, key.statId.value)
        val value = buffer[bufferedKey] ?: definition.defaultValue.toDouble()
        return CompletableDeferred(toNumber(value, definition.type))
    }

    override fun getPlayerStats(playerId: UUID, gameId: GameId): Deferred<Map<StatId, Number>> {
        val definitions = getDefinitionsForGame(gameId)
        if (definitions.isEmpty()) return CompletableDeferred(emptyMap())

        val result = definitions.associate { def ->
            val bufferedKey = BufferedStatKey(playerId, gameId.value, def.id)
            val value = buffer[bufferedKey] ?: def.defaultValue.toDouble()
            StatId(def.id) to toNumber(value, def.type)
        }
        return CompletableDeferred(result)
    }

    override fun getLeaderboard(key: StatKey, limit: Int): Deferred<List<Pair<UUID, Number>>> {
        val definition = getDefinition(key) ?: return CompletableDeferred(emptyList())

        val entries = buffer.entries
            .filter { it.key.gameId == key.gameId.value && it.key.statId == key.statId.value }
            .map { it.key.playerId to toNumber(it.value, definition.type) }

        val sorted = if (definition.aggregation == StatAggregation.MAX || definition.aggregation == StatAggregation.SUM) {
            entries.sortedByDescending { it.second.toDouble() }
        } else {
            entries.sortedBy { it.second.toDouble() }
        }
        return CompletableDeferred(sorted.take(limit))
    }

    /**
     * Flush buffered stats to the target service and clear the buffer.
     * Must be called from a coroutine on the database dispatcher.
     */
    fun flush(target: StatsServiceImpl) {
        val snapshot = buffer.toMap()
        buffer.clear()
        for ((key, value) in snapshot) {
            target.recordSync(StatScope.Player(key.playerId), StatKey(GameId(key.gameId), StatId(key.statId)), value)
        }
    }

    fun hasBufferedData(): Boolean = buffer.isNotEmpty()

    private fun getDefinition(key: StatKey): StatDefinition? =
        getDefinitionsForGame(key.gameId).find { it.id == key.statId.value }

    private fun getDefinitionsForGame(gameId: GameId): List<StatDefinition> =
        loadedGames
            .filter { it.getProperties().id == gameId.value }
            .flatMap { it.getStatDefinitions() }

    private fun toNumber(value: Double, type: StatType): Number = when (type) {
        StatType.IntStat -> value.toInt()
        StatType.LongStat -> value.toLong()
        StatType.DoubleStat -> value
    }
}
