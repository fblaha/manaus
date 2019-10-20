package cz.fb.manaus.core.provider

import com.fasterxml.jackson.annotation.JsonIgnore

data class ExchangeProvider(
        val name: String,
        val minAmount: Double,
        val minPrice: Double,
        val commission: Double,
        val capabilities: Set<ProviderCapability> = emptySet()) {

    fun validate() {
        validateProviderCapabilities(capabilities)
    }

    @JsonIgnore
    val capabilityPredicate: CapabilityPredicate = predicate(capabilities)

}


