package cz.fb.manaus.core.provider

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val capabilities: Set<ProviderCapability> = emptySet()
)

enum class ProviderCapability {
    FixedStepPrice,
    LastMatchedPrice,
    TradedVolume,
}