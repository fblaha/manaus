package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.provider.ProviderSelector

interface RoundingPlugin : ProviderSelector {

    fun shift(price: Double, steps: Int): Double?

    fun round(price: Double): Double?
}
