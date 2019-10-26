package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

const val chargeRate = 0.02

class ProfitServiceTest : AbstractLocalTestCase() {
    @Autowired
    private lateinit var profitService: ProfitService


    @Test
    fun `single selection`() {
        val lay = homeSettledBet.copy(
                profit = 5.0,
                commission = 0.5 * chargeRate,
                price = Price(2.0, 4.0, Side.LAY)
        )
        val back = lay.copy(
                profit = -4.5,
                commission = 0.0,
                price = Price(2.2, 3.5, Side.BACK),
                id = "222"
        )
        checkRecords(withCharge(0.5), lay, back)
    }

    @Test
    fun `multi selection`() {
        val layHome = homeSettledBet.copy(profit = 5.0,
                id = "111",
                commission = 1 * chargeRate / 2, // 2% net win - 2 win bets
                price = Price(2.0, 4.0, Side.LAY))
        val backHome = homeSettledBet.copy(profit = -4.1,
                id = "222",
                price = Price(2.2, 3.5, Side.BACK))
        val layDraw = drawSettledBet.copy(profit = 5.0,
                id = "333",
                commission = 1 * chargeRate / 2,
                price = Price(2.0, 4.0, Side.LAY))
        val backDraw = drawSettledBet.copy(profit = -4.9,
                id = "444",
                price = Price(2.2, 3.5, Side.BACK))
        val records = checkRecords(withCharge(1.0), layHome, backHome, layDraw, backDraw)
        checkCategories(records, "selectionRegexp_draw" to 0.09)
    }


    @Test
    fun `real back win`() {
        //22263	2012-04-26 15:37:47.0	2012-04-26 06:11:23.0	6.4	    2.14	1	-7.3	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        val lay = homeSettledBet.copy(selectionId = SEL_HOME,
                selectionName = "Over 2.5 Goals",
                profit = -7.3,
                id = "22263",
                price = Price(2.14, 6.4, Side.LAY))
        //22264	2012-04-26 15:53:26.0	2012-04-26 15:53:07.0	5.44	2.34	0	7.29	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        val back = lay.copy(profit = 7.29,
                id = "22264",
                price = Price(2.34, 5.44, Side.BACK))
        checkRecords(withCharge(-0.01), lay, back)
    }

    @Test
    fun `real lay win`() {
        val kamazLay = createKamazLay()
        val kamazBack = createKamazBack()
        checkRecords(withCharge(1.05), kamazLay, kamazBack)
    }

    private fun createKamazBack(): SettledBet {
        //22256	2012-04-26 12:40:33.0	2012-04-26 12:14:07.0	4.22	2.98	0	-4.22	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return homeSettledBet.copy(selectionId = SEL_HOME,
                id = "kmzBack",
                selectionName = "Kamaz",
                profit = -4.22,
                price = Price(2.98, 4.22, Side.BACK))
    }

    private fun createKamazLay(): SettledBet {
        //22255	2012-04-26 06:56:08.0	2012-04-26 02:00:51.0	5.27	2.92	1	5.27	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return homeSettledBet.copy(selectionId = SEL_HOME,
                selectionName = "Kamaz",
                profit = 5.27,
                commission = 0.021,
                id = "kmzLay",
                price = Price(2.92, 5.27, Side.LAY))
    }

    @Test
    fun simulation() {
        val kamazBack = createKamazBack()
        val kamazLay = createKamazLay()
        val bets = listOf(kamazBack, kamazLay).map { toRealizedBet(it) }
        val simulationOnly = profitService.getProfitRecords(
                bets = bets,
                simulationAwareOnly = true)
        val all = profitService.getProfitRecords(
                bets = bets,
                simulationAwareOnly = false)
        assertTrue(simulationOnly.size < all.size)
        assertTrue(simulationOnly.isNotEmpty())
    }

    private fun checkCategories(records: List<ProfitRecord>, vararg expectedProfits: Pair<String, Double>) {
        val byCategory = byCategory(records)
        for ((key, value) in expectedProfits) {
            val record = byCategory[key]!!
            Assert.assertEquals(value, record.profit, 0.0001)
        }
    }

    private fun checkRecords(expectedAllProfit: Double, vararg bets: SettledBet): List<ProfitRecord> {
        val betList = listOf(*bets).map { toRealizedBet(it) }
        val result = profitService.getProfitRecords(betList, null, false)
        val all = result.find { ProfitRecord.isAllCategory(it) }!!
        Assert.assertEquals(expectedAllProfit, all.profit, 0.01)
        val backCount = betList.filter { it.settledBet.price.side === Side.BACK }.count()
        val layCount = betList.filter { it.settledBet.price.side === Side.LAY }.count()
        assertEquals(backCount, all.backCount)
        assertEquals(layCount, all.layCount)
        assertEquals(layCount + backCount, all.totalCount)
        return result
    }

    @Test
    fun `get profit records`() {
        val bet1 = drawSettledBet.copy(
                profit = 5.0,
                commission = 3.0 * chargeRate,
                price = Price(2.0, 4.0, Side.LAY))
        val bet2 = drawSettledBet.copy(
                profit = -2.0,
                commission = 0.0,
                id = "bet2",
                price = Price(2.0, 5.0, Side.BACK))
        val bets = listOf(bet1, bet2).map { toRealizedBet(it) }
        val records = profitService.getProfitRecords(
                bets = bets,
                simulationAwareOnly = true)

        val byCategory = byCategory(records)

        assertEquals(1, byCategory["market_country_cz"]!!.layCount)
        Assert.assertEquals(withCharge(3.0), byCategory["market_country_cz"]!!.profit, 0.01)

        assertTrue(profitService.getProfitRecords(bets = bets,
                projection = "market_country_ua",
                simulationAwareOnly = true).isEmpty())
        assertFalse(profitService.getProfitRecords(bets = bets,
                projection = "market_country_cz",
                simulationAwareOnly = true).isEmpty())
    }

    private fun byCategory(records: List<ProfitRecord>): Map<String, ProfitRecord> {
        return records.map { it.category to it }.toMap()
    }

    @Test
    fun `merge category`() {
        val r1 = ProfitRecord("test", 100.0, 2.0, 0.06, 1, 1)
        r1.coverDiff = 0.2
        r1.coverCount = 1
        val r2 = ProfitRecord("test", 100.0, 2.0, 0.06, 1, 1)
        val record = profitService.mergeCategory("test", listOf(r1, r2))
        Assert.assertEquals(record.coverDiff!!, r1.coverDiff!!, 0.00001)
    }
}

fun withCharge(profit: Double, rate: Double = chargeRate): Double {
    return when {
        profit > 0 -> profit - profit * rate
        else -> profit
    }
}