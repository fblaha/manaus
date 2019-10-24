package cz.fb.manaus.core.provider

interface ProviderSelector {

    val tags: Set<ProviderTag> get() = emptySet()

}