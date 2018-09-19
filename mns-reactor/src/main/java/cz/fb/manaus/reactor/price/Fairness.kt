package cz.fb.manaus.reactor.price

import com.google.common.base.MoreObjects
import cz.fb.manaus.core.model.Side
import java.util.*
import java.util.Objects.requireNonNull

class Fairness(val back: OptionalDouble, val lay: OptionalDouble) {

    val moreCredibleSide: Optional<Side>
        get() {
            if (lay.isPresent && back.isPresent) {
                val layInverted = 1 / lay.asDouble
                return if (back.asDouble > layInverted) {
                    Optional.of(Side.BACK)
                } else {
                    Optional.of(Side.LAY)
                }
            } else if (lay.isPresent) {
                return Optional.of(Side.LAY)
            } else if (back.isPresent) {
                return Optional.of(Side.BACK)
            }
            return Optional.empty()
        }

    operator fun get(side: Side): OptionalDouble {
        return if (requireNonNull(side) === Side.BACK) back else lay
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
                .add("back", back)
                .add("lay", lay)
                .toString()
    }
}
