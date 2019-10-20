package cz.fb.manaus.reactor.rounding

import cz.fb.manaus.core.provider.RequiredCapabilitiesAware

interface RoundingPlugin : RequiredCapabilitiesAware {

    fun shift(price: Double, steps: Int): Double?

    fun round(price: Double): Double?
}
