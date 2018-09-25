package cz.fb.manaus.reactor.profit

import com.google.common.collect.ImmutableMap.of
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.ProfitRecord
import cz.fb.manaus.core.model.SettledBet
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.core.test.ModelFactory.Companion.newSettled
import org.apache.commons.lang3.time.DateUtils.addDays
import org.apache.commons.lang3.time.DateUtils.addHours
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfitServiceTest : AbstractProfitTest() {
    @Autowired
    private lateinit var profitService: ProfitService
    @Autowired
    private lateinit var provider: ExchangeProvider


    @Test
    fun `single selection`() {
        val lay = newSettled(CoreTestFactory.DRAW, "The Draw", 5.0, marketDate, Price(2.0, 4.0, Side.LAY))
        val back = newSettled(CoreTestFactory.DRAW, "The Draw", -4.5, marketDate, Price(2.2, 3.5, Side.BACK))
        setBetAction(lay, back)
        checkRecords(0.47, null, lay, back)
    }

    @Test
    fun `multi selection`() {
        val layDraw = newSettled(CoreTestFactory.DRAW, "The Draw", 5.0, marketDate, Price(2.0, 4.0, Side.LAY))
        val backDraw = newSettled(CoreTestFactory.DRAW, "The Draw", -4.9, marketDate, Price(2.2, 3.5, Side.BACK))
        val layHome = newSettled(CoreTestFactory.HOME, "Home", 5.0, marketDate, Price(2.0, 4.0, Side.LAY))
        val backHome = newSettled(CoreTestFactory.HOME, "Home", -4.1, marketDate, Price(2.2, 3.5, Side.BACK))
        setBetAction(layDraw, backDraw, layHome, backHome)
        checkRecords(0.935, of("selectionRegexp_draw", 0.067), layHome, backHome, layDraw, backDraw)
    }


    @Test
    fun `real back win`() {
        //22263	2012-04-26 15:37:47.0	2012-04-26 06:11:23.0	6.4	    2.14	1	-7.3	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        val lay = newSettled(CoreTestFactory.DRAW, "Over 2.5 Goals", -7.3, marketDate, Price(2.14, 6.4, Side.LAY))
        //22264	2012-04-26 15:53:26.0	2012-04-26 15:53:07.0	5.44	2.34	0	7.29	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        val back = newSettled(CoreTestFactory.DRAW, "Over 2.5 Goals", 7.29, marketDate, Price(2.34, 5.44, Side.BACK))
        setBetAction(lay, back)
        checkRecords(-0.01, null, lay, back)
    }

    @Test
    fun `real lay win`() {
        val kamazLay = createKamazLay()
        val kamazBack = createKamazBack()
        setBetAction(kamazBack, kamazLay)
        checkRecords(0.98, null, kamazLay, kamazBack)
    }

    private fun createKamazBack(): SettledBet {
        //22256	2012-04-26 12:40:33.0	2012-04-26 12:14:07.0	4.22	2.98	0	-4.22	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return newSettled(CoreTestFactory.DRAW, "Kamaz", -4.22, marketDate, Price(2.98, 4.22, Side.BACK))
    }

    private fun createKamazLay(): SettledBet {
        //22255	2012-04-26 06:56:08.0	2012-04-26 02:00:51.0	5.27	2.92	1	5.27	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return newSettled(CoreTestFactory.DRAW, "Kamaz", 5.27, marketDate, Price(2.92, 5.27, Side.LAY))
    }

    @Test
    fun `simulation`() {
        val kamazBack = createKamazBack()
        val kamazLay = createKamazLay()
        setBetAction(kamazBack, kamazLay)
        val bets = listOf(kamazBack, kamazLay)
        val simulationOnly = profitService.getProfitRecords(bets, null, true,
                provider.chargeRate)
        val all = profitService.getProfitRecords(bets, null, false,
                provider.chargeRate)
        assertTrue(simulationOnly.size < all.size)
        assertTrue(simulationOnly.size > 0)
    }

    private fun checkRecords(expectedAllProfit: Double, otherProfits: Map<String, Double>?, vararg bets: SettledBet) {
        val betList = listOf(*bets)
        val result = profitService.getProfitRecords(betList, null,
                false, provider.chargeRate)
        val all = result.find { ProfitRecord.isAllCategory(it) }!!
        Assert.assertEquals(expectedAllProfit, all.profit, 0.01)
        val backCount = betList.filter { bet -> bet.price.side === Side.BACK }.count()
        val layCount = betList.filter { bet -> bet.price.side === Side.LAY }.count()
        assertEquals(backCount, all.backCount)
        assertEquals(layCount, all.layCount)
        assertEquals(layCount + backCount, all.totalCount)
        if (otherProfits != null) {
            val byCategory = byCategory(result)
            for ((key, value) in otherProfits) {
                val record = byCategory[key]!!
                Assert.assertEquals(value, record.profit, 0.0001)
            }
        }
    }

    @Test
    fun `get profit records`() {
        val bet1 = newSettled(CoreTestFactory.HOME, "The Draw", 5.0,
                addDays(marketDate, 1), Price(2.0, 4.0, Side.LAY))
        bet1.placed = addDays(marketDate, -1)
        val bet2 = newSettled(CoreTestFactory.HOME, "The Draw", -2.0,
                addDays(marketDate, 1), Price(2.0, 5.0, Side.BACK))
        bet2.placed = addHours(marketDate, -1)
        setBetAction(bet1, bet2)
        val records = profitService.getProfitRecords(listOf(bet1, bet2), null, true,
                provider.chargeRate)

        val byCategory = byCategory(records)


        assertEquals(1, byCategory["market_country_br"]!!.layCount)
        Assert.assertEquals(2.8, byCategory["market_country_br"]!!.profit, 0.01)

        assertEquals(1, byCategory["placedBefore_hour_1-2"]!!.backCount)
        Assert.assertEquals(-2.0, byCategory["placedBefore_hour_1-2"]!!.profit, 0.01)

        assertEquals(1, byCategory["placedBefore_day_1-2"]!!.layCount)
        Assert.assertEquals(4.8, byCategory["placedBefore_day_1-2"]!!.profit, 0.01)

        assertTrue(profitService.getProfitRecords(listOf(bet1, bet2), "market_country_ua", true,
                provider.chargeRate).isEmpty())
        assertFalse(profitService.getProfitRecords(listOf(bet1, bet2), "market_country_br", true,
                provider.chargeRate).isEmpty())

    }

    private fun byCategory(records: List<ProfitRecord>): Map<String, ProfitRecord> {
        return records.map { it.category to it }.toMap()
    }

    @Test
    fun `merge category`() {
        val r1 = ProfitRecord("test", 100.0, 1, 1, 2.0, 0.06)
        r1.coverDiff = 0.2
        r1.coverCount = 1
        val r2 = ProfitRecord("test", 100.0, 1, 1, 2.0, 0.06)
        val record = profitService.mergeCategory("test", listOf(r1, r2))
        Assert.assertEquals(record.coverDiff, r1.coverDiff, 0.00001)
    }
}
