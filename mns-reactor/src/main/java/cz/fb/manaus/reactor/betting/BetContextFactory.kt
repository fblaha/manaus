package cz.fb.manaus.reactor.betting

import cz.fb.manaus.core.model.AccountMoney
import cz.fb.manaus.core.model.MarketSnapshot
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.reactor.charge.ChargeGrowthForecaster
import cz.fb.manaus.reactor.price.Fairness
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class BetContextFactory {

    @Autowired
    private lateinit var forecaster: ChargeGrowthForecaster

    fun create(side: Side,
               selectionId: Long,
               snapshot: MarketSnapshot,
               fairness: Fairness,
               accountMoney: Optional<AccountMoney>,
               categoryBlacklist: Set<String>): BetContext {
        val forecast = forecaster.getForecast(selectionId, side, snapshot, fairness)
        return BetContext(side, selectionId, accountMoney, forecast, snapshot, fairness,
                categoryBlacklist)
    }
}
