package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.betting.makeName

interface ProgressFunction : (RealizedBet) -> Double? {

    val name: String
        get() {
            return makeName(this).removeSuffix("Function")
        }

    val includeNoValues: Boolean get() = true

}
