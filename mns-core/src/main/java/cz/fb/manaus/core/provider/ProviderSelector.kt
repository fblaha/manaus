package cz.fb.manaus.core.provider

interface ProviderSelector {

    val requiredCapabilities: Set<ProviderCapability> get() = emptySet()

}