package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.SettledBet

interface ProfitPlugin {

    fun getCharges(bets: List<SettledBet>, chargeRate: Double): Map<String, Double>

}
