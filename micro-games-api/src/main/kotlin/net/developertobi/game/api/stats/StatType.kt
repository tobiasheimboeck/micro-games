package net.developertobi.game.api.stats

sealed class StatType {
    object IntStat : StatType()
    object LongStat : StatType()
    object DoubleStat : StatType()
}

enum class StatAggregation {
    SUM, MAX, MIN, LAST
}

data class StatDefinition(
    val id: String,
    val type: StatType,
    val aggregation: StatAggregation,
    val displayKey: String,
    val defaultValue: Number = 0
)
