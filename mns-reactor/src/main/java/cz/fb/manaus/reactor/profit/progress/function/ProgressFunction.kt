package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.reactor.betting.NameAware

interface ProgressFunction : NameAware, (RealizedBet) -> Double? {

    override val name: String
        get() {
            return super.name.removeSuffix("Function")
        }
}
