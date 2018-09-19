package cz.fb.manaus.reactor.rounding

import java.util.*

interface RoundingPlugin {

    fun shift(price: Double, steps: Int): OptionalDouble

    fun round(price: Double): OptionalDouble
}
