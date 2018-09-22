package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.reactor.betting.NameAware

interface ProgressFunction : NameAware, (SettledBet) -> Double? {

    override val name: String
        get() {
            return super.name.removeSuffix("Function")
        }
}
