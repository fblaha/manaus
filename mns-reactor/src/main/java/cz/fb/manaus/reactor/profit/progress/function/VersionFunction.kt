package cz.fb.manaus.reactor.profit.progress.function

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object VersionFunction : ProgressFunction {

    override fun invoke(bet: RealizedBet): Double? {
        return when (val version = bet.betAction.version) {
            0 -> null
            else -> version.toDouble()
        }
    }

}
