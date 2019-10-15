package cz.fb.manaus.core.provider

interface RequiredCapabilitiesAware {

    val requiredCapabilities: Set<ProviderCapability>
        get() = emptySet()

    fun checkCapabilities(providerCapabilities: Set<ProviderCapability>): Boolean {
        return providerCapabilities.containsAll(requiredCapabilities)
    }

}