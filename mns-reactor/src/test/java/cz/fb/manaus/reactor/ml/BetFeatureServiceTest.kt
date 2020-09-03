package cz.fb.manaus.reactor.ml

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.drawSettledBet
import cz.fb.manaus.core.test.AbstractTestCase
import cz.fb.manaus.reactor.profit.toRealizedBet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class BetFeatureServiceTest : AbstractTestCase() {

    @Autowired
    private lateinit var betFeatureService: BetFeatureService

    @Test
    fun toFeatureVector() {
        val featureVector = betFeatureService.toFeatureVector(toRealizedBet(drawSettledBet))
        assertEquals(Side.BACK, featureVector.side)
        assertTrue(featureVector.features.isNotEmpty())
    }
}