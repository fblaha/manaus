package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer
import cz.fb.manaus.core.model.SettledBet
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.*

@Component
class RandomCategorizer : SettledBetCategorizer {

    private val random = Random(Clock.systemUTC().millis())

    override val isSimulationSupported: Boolean = false

    override fun getCategories(settledBet: SettledBet, coverage: BetCoverage): Set<String> {
        val randInt = random.nextInt(5)
        return setOf("random_$randInt")
    }

}
