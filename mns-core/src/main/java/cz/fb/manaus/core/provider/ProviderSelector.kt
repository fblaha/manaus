package cz.fb.manaus.core.provider

interface ProviderSelector {

    val tags: Set<String> get() = emptySet()

    val capabilities: Set<ProviderCapability> get() = emptySet()

}