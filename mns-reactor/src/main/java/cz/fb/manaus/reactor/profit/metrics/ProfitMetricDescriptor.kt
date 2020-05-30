package cz.fb.manaus.reactor.profit.metrics

data class ProfitMetricDescriptor(
        val interval: String,
        val categoryPrefix: String,
        val categoryValues: Set<String>,
        val projection: String? = null
) {
    val metricName = "mns_profit_${categoryPrefix}_${interval}"

    fun extractVal(category: String): String =
            category.removePrefix(categoryPrefix).trim { !it.isLetterOrDigit() }
}