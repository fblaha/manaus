package cz.fb.manaus.core.metrics

data class MetricRecord<out T>(val name: String, val value: T)
