package net.developertobi.game.bukkit.api.stats

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
import net.developertobi.game.bukkit.database.PlayerStatsTable
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class StatsServiceImpl(
    private val plugin: JavaPlugin,
    private val loadedGames: List<MicroGame>,
    private val scope: CoroutineScope,
) : StatsService {

    override fun record(scope: StatScope.Player, key: StatKey, value: Number) {
        this.scope.launch(Dispatchers.IO) {
            try {
                applyRecord(scope, key, value)
            } catch (e: Exception) {
                plugin.logger.warning("Failed to record stat: ${e.message}")
            }
        }
    }

    /**
     * Synchronously apply a record. Used by BufferedStatsService when flushing.
     */
    internal fun recordSync(scope: StatScope.Player, key: StatKey, value: Number) {
        applyRecord(scope, key, value)
    }

    private fun applyRecord(scope: StatScope.Player, key: StatKey, value: Number) {
        val definition = getDefinition(key) ?: return

        transaction {
                    val currentRow = PlayerStatsTable.selectAll()
                        .where {
                            (PlayerStatsTable.playerId eq scope.playerId.toString()) and
                                (PlayerStatsTable.gameId eq key.gameId.value) and
                                (PlayerStatsTable.statId eq key.statId.value)
                        }
                        .singleOrNull()

                    val currentVal = currentRow?.get(PlayerStatsTable.value)?.toDouble() ?: definition.defaultValue.toDouble()
                    val newVal = when (definition.aggregation) {
                        StatAggregation.SUM -> currentVal + value.toDouble()
                        StatAggregation.MAX -> maxOf(currentVal, value.toDouble())
                        StatAggregation.MIN -> minOf(currentVal, value.toDouble())
                        StatAggregation.LAST -> value.toDouble()
                    }

                    if (currentRow != null) {
                        PlayerStatsTable.update(
                            where = {
                                (PlayerStatsTable.playerId eq scope.playerId.toString()) and
                                    (PlayerStatsTable.gameId eq key.gameId.value) and
                                    (PlayerStatsTable.statId eq key.statId.value)
                            }
                        ) {
                            it[PlayerStatsTable.value] = java.math.BigDecimal(newVal.toString())
                        }
                    } else {
                        PlayerStatsTable.insert {
                            it[playerId] = scope.playerId.toString()
                            it[gameId] = key.gameId.value
                            it[statId] = key.statId.value
                            it[PlayerStatsTable.value] = java.math.BigDecimal(newVal.toString())
                        }
                    }
                }
    }

    override fun get(scope: StatScope.Player, key: StatKey): Deferred<Number> {
        val definition = getDefinition(key) ?: return CompletableDeferred(0)

        return this.scope.async(Dispatchers.IO) {
            transaction {
                val row = PlayerStatsTable.selectAll()
                    .where {
                        (PlayerStatsTable.playerId eq scope.playerId.toString()) and
                            (PlayerStatsTable.gameId eq key.gameId.value) and
                            (PlayerStatsTable.statId eq key.statId.value)
                    }
                    .singleOrNull()

                val value = row?.get(PlayerStatsTable.value)?.toDouble() ?: definition.defaultValue.toDouble()
                toNumber(value, definition.type)
            }
        }
    }

    override fun getPlayerStats(playerId: UUID, gameId: GameId): Deferred<Map<StatId, Number>> {
        val definitions = getDefinitionsForGame(gameId)
        if (definitions.isEmpty()) return CompletableDeferred(emptyMap())

        return scope.async(Dispatchers.IO) {
            transaction {
                val rows = PlayerStatsTable.selectAll()
                    .where {
                        (PlayerStatsTable.playerId eq playerId.toString()) and
                            (PlayerStatsTable.gameId eq gameId.value)
                    }

                rows.mapNotNull { row: ResultRow ->
                    val statIdStr = row[PlayerStatsTable.statId]
                    val def = definitions.find { it.id == statIdStr } ?: return@mapNotNull null
                    StatId(statIdStr) to toNumber(row[PlayerStatsTable.value].toDouble(), def.type)
                }.toMap()
            }
        }
    }

    override fun getLeaderboard(key: StatKey, limit: Int): Deferred<List<Pair<UUID, Number>>> {
        val definition = getDefinition(key) ?: return CompletableDeferred(emptyList())

        return scope.async(Dispatchers.IO) {
            transaction {
                val descending = definition.aggregation == StatAggregation.MAX || definition.aggregation == StatAggregation.SUM
                val sortOrder = if (descending) SortOrder.DESC else SortOrder.ASC
                val rows = PlayerStatsTable.selectAll()
                    .where {
                        (PlayerStatsTable.gameId eq key.gameId.value) and
                            (PlayerStatsTable.statId eq key.statId.value)
                    }
                    .orderBy(PlayerStatsTable.value to sortOrder)
                    .limit(limit)

                rows.mapNotNull { row: ResultRow ->
                    val uuid = runCatching { UUID.fromString(row[PlayerStatsTable.playerId]) }.getOrNull()
                        ?: return@mapNotNull null
                    uuid to toNumber(row[PlayerStatsTable.value].toDouble(), definition.type)
                }
            }
        }
    }

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
