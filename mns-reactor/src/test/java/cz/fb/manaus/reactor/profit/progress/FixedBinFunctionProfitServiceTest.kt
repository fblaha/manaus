package cz.fb.manaus.reactor.profit.progress

import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.profit.generateBets
import cz.fb.manaus.reactor.profit.toRealizedBet
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class FixedBinFunctionProfitServiceTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var service: FixedBinFunctionProfitService
    @Autowired
    private lateinit var provider: ExchangeProvider

    @Test
    fun `single bin`() {
        val bets = generateBets().map { toRealizedBet(it) }
        val records = service.getProfitRecords(bets = bets,
                funcName = "priceBack",
                binCount = 1)
        assertEquals(1, records.size)
        assertEquals("priceBack: 2.84", records[0].category)
        assertEquals(bets.size / 2, records[0].totalCount)
        assertEquals(bets.size / 2, records[0].backCount)
        assertEquals(0, records[0].layCount)
    }

    @Test
    fun `multiple bins`() {
        val bets = generateBets().map { toRealizedBet(it) }
        val records = service.getProfitRecords(bets = bets,
                funcName = "priceBack",
                binCount = 10)
        assertEquals(10, records.size)
        assertTrue(records.zipWithNext().all { it.first.avgPrice < it.second.avgPrice })
    }
}