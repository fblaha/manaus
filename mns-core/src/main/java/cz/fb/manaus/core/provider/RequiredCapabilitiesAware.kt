package cz.fb.manaus.core.provider

interface RequiredCapabilitiesAware {

    val requiredCapabilities: Set<ProviderCapability> get() = emptySet()

}