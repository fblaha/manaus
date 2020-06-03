package cz.fb.manaus.reactor.profit.metrics

import cz.fb.manaus.core.model.ProfitRecord


enum class UpdateFrequency {
    LOW, MEDIUM, HIGH
}

data class ProfitQuery(
        val updateFrequency: UpdateFrequency,
        val interval: String,
        val projection: String? = null
)

data class ProfitMetricSpec(
        val query: ProfitQuery,
        val categoryPrefix: String,
        val categoryValues: Set<String>
) {
    val metricName = "mns_profit_${categoryPrefix}_${query.interval}"

    val recordPredicate: (ProfitRecord) -> Boolean = { it.category.startsWith(categoryPrefix) }

    fun extractVal(category: String): String =
            category.removePrefix(categoryPrefix).trim { !it.isLetterOrDigit() }
}