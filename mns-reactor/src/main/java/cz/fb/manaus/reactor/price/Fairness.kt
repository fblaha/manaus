package cz.fb.manaus.reactor.price

import com.google.common.base.MoreObjects
import cz.fb.manaus.core.model.Side
import java.util.*
import java.util.Objects.requireNonNull

class Fairness(val back: Double?, val lay: Double?) {

    val moreCredibleSide: Optional<Side>
        get() {
            if (lay != null && back != null) {
                val layInverted = 1 / lay
                return if (back > layInverted) {
                    Optional.of(Side.BACK)
                } else {
                    Optional.of(Side.LAY)
                }
            } else if (lay != null) {
                return Optional.of(Side.LAY)
            } else if (back != null) {
                return Optional.of(Side.BACK)
            }
            return Optional.empty()
        }

    operator fun get(side: Side): Double? {
        return if (requireNonNull(side) === Side.BACK) back else lay
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("back", back)
                .add("lay", lay)
                .toString()
    }
}
