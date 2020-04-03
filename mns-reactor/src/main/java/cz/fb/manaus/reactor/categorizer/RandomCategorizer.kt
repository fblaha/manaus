package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.category.categorizer.RealizedBetCategorizer
import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.*

@Component
class RandomCategorizer : RealizedBetCategorizer {

    private val random = Random(Clock.systemUTC().millis())

    override val isSimulationSupported: Boolean = false

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val randInt = random.nextInt(5)
        return setOf("random_$randInt")
    }

}
