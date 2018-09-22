package cz.fb.manaus.reactor.profit.progress

import com.google.common.collect.Comparators
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.reactor.profit.AbstractProfitTest
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class ProgressProfitServiceTest : AbstractProfitTest() {

    @Autowired
    private lateinit var service: ProgressProfitService
    @Autowired
    private lateinit var provider: ExchangeProvider


    @Test
    fun `single chunk`() {
        val bets = generateBets(Optional.empty<Side>())
        val records = service.getProfitRecords(bets,
                Optional.of("price"), 1, provider.chargeRate, Optional.empty())
        assertEquals(1, records.size)
        assertEquals("price: 2.79", records[0].category)
        assertEquals(bets.size, records[0].totalCount)
        assertEquals(bets.size / 2, records[0].backCount)
    }

    @Test
    fun `multiple chunks`() {
        val bets = generateBets(Optional.empty<Side>())
        val records = service.getProfitRecords(bets,
                Optional.of("price"), 10, provider.chargeRate, Optional.empty())
        assertEquals(10, records.size)
        assertTrue(Comparators.isInStrictOrder(records, compareBy { it.avgPrice }))
    }
}