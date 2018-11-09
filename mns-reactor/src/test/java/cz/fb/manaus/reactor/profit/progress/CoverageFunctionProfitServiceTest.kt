package cz.fb.manaus.reactor.profit.progress

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.profit.AbstractProfitTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals


class CoverageFunctionProfitServiceTest : AbstractProfitTest() {

    @Autowired
    private lateinit var service: CoverageFunctionProfitService
    @Autowired
    private lateinit var provider: ExchangeProvider

    @Test
    fun `covered price`() {
        val bets = generateBets()
        val records = service.getProfitRecords(bets,
                "price", provider.chargeRate)
        assertEquals(2, records.size)
        assertEquals("price_covered: 2.79", records[0].category)
        assertEquals(bets.size, records[0].totalCount)
        assertEquals(bets.size / 2, records[0].backCount)
    }

    @Test
    fun `solo price`() {
        val bets = generateBets(Side.BACK)
        val records = service.getProfitRecords(bets,
                "price", provider.chargeRate)
        assertEquals(1, records.size)
        assertEquals("price_solo: 2.84", records[0].category)
        assertEquals(bets.size, records[0].totalCount)
        assertEquals(bets.size, records[0].backCount)
    }

}