package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.makeName
import cz.fb.manaus.core.model.RealizedBet

interface ProgressFunction : (RealizedBet) -> Double? {

    val name: String
        get() = makeName(this).removeSuffix("Function")

    val includeNoValues: Boolean get() = true

}
