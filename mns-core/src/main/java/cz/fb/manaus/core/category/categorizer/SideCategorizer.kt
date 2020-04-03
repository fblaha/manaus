package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.model.RealizedBet
import org.springframework.stereotype.Component

@Component
object SideCategorizer : RealizedBetCategorizer {

    override fun getCategories(realizedBet: RealizedBet): Set<String> {
        val side = realizedBet.settledBet.price.side.name.toLowerCase()
        return setOf("side_$side")
    }

}
