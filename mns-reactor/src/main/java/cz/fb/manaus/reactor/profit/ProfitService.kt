package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.category.BetCoverage
import cz.fb.manaus.core.category.CategoryService
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.Side
import org.springframework.stereotype.Service


@Service
class ProfitService(private val categoryService: CategoryService) {

    fun getProfitRecords(bets: List<RealizedBet>, projection: String? = null, simulationAwareOnly: Boolean): List<ProfitRecord> {
        var filtered = bets
        val coverage = BetCoverage.from(filtered)

        if (projection != null) {
            filtered = categoryService.filterBets(filtered, projection, coverage)
        }

        val betRecords = computeProfitRecords(filtered, simulationAwareOnly, coverage)
        return mergeProfitRecords(betRecords)
    }

    private fun mergeProfitRecords(records: Collection<ProfitRecord>): List<ProfitRecord> {
        val categories = records.groupBy { it.category }
        return categories.entries
                .map { mergeCategory(it.key, it.value) }
                .sortedBy { it.category }
    }

    fun mergeCategory(category: String, records: Collection<ProfitRecord>): ProfitRecord {
        require(records.map { it.category }
                .all { category == it })
        val avgPrice = records
                .map { it.avgPrice }
                .average()
        val theoreticalProfit = records.map { it.theoreticalProfit }.sum()
        val charge = records.map { it.charge }.sum()
        val layCount = records.map { it.layCount }.sum()
        val backCount = records.map { it.backCount }.sum()
        val coverCount = records.map { it.coverCount }.sum()
        val result = ProfitRecord(category, theoreticalProfit, avgPrice, charge, layCount, backCount)
        if (coverCount > 0) {
            val diff = records.filter { it.coverDiff != null }.mapNotNull { it.coverDiff }.average()
            result.coverDiff = diff
            result.coverCount = coverCount
        }
        return result
    }

    private fun computeProfitRecords(bets: List<RealizedBet>, simulationAwareOnly: Boolean, coverage: BetCoverage): List<ProfitRecord> {
        return bets.flatMap { bet ->
            val categories = categoryService.getRealizedBetCategories(bet, simulationAwareOnly, coverage)
            categories.map {
                val charge = bet.settledBet.commission ?: 0.0
                check(charge >= 0) { charge }
                toProfitRecord(bet, it, charge, coverage)
            }
        }
    }

    fun toProfitRecord(bet: RealizedBet, category: String, chargeContribution: Double, coverage: BetCoverage): ProfitRecord {
        val side = bet.settledBet.price.side
        val price = bet.settledBet.price.price
        val result = if (side === Side.BACK) {
            ProfitRecord(category, bet.settledBet.profit, price, chargeContribution, 0, 1)
        } else {
            ProfitRecord(category, bet.settledBet.profit, price, chargeContribution, 1, 0)
        }
        val marketId = bet.market.id
        val selectionId = bet.settledBet.selectionId
        if (coverage.isCovered(marketId, selectionId)) {
            val backPrice = coverage.getPrice(marketId, selectionId, Side.BACK)
            val layPrice = coverage.getPrice(marketId, selectionId, Side.LAY)
            result.coverDiff = backPrice - layPrice
            result.coverCount = 1
        }
        return result
    }

}
