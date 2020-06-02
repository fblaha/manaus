package cz.fb.manaus.reactor.profit.metrics

import cz.fb.manaus.core.model.ProfitRecord


enum class UpdateFrequency {
    LOW, MEDIUM, HIGH
}

data class ProfitMetricSpec(
        val interval: String,
        val updateFrequency: UpdateFrequency,
        val categoryPrefix: String,
        val categoryValues: Set<String>
) {
    val metricName = "mns_profit_${categoryPrefix}_${interval}"

    val recordPredicate: (ProfitRecord) -> Boolean = { it.category.startsWith(categoryPrefix) }

    fun extractVal(category: String): String =
            category.removePrefix(categoryPrefix).trim { !it.isLetterOrDigit() }
}