package cz.fb.manaus.reactor.rounding

interface RoundingPlugin {

    fun shift(price: Double, steps: Int): Double?

    fun round(price: Double): Double?
}
