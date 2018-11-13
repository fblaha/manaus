package cz.fb.manaus.reactor.price

import cz.fb.manaus.core.model.Side
import java.util.Objects.requireNonNull

data class Fairness(val back: Double? = null, val lay: Double? = null) {

    val moreCredibleSide: Side?
        get() {
            if (lay != null && back != null) {
                val layInverted = 1 / lay
                return if (back > layInverted) {
                    Side.BACK
                } else {
                    Side.LAY
                }
            } else if (lay != null) {
                return Side.LAY
            } else if (back != null) {
                return Side.BACK
            }
            return null
        }

    operator fun get(side: Side): Double? {
        return if (requireNonNull(side) === Side.BACK) back else lay
    }
}
