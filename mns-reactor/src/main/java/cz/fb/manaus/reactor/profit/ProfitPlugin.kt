package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.RealizedBet

interface ProfitPlugin {

    fun getCharges(bets: List<RealizedBet>, chargeRate: Double): Map<String, Double>

}
