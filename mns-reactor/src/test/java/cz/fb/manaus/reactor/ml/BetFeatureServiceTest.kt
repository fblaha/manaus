package cz.fb.manaus.reactor.ml

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.drawSettledBet
import cz.fb.manaus.core.test.AbstractTestCase5
import cz.fb.manaus.reactor.profit.toRealizedBet
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BetFeatureServiceTest : AbstractTestCase5() {

    @Autowired
    private lateinit var betFeatureService: BetFeatureService

    @Test
    fun toFeatureVector() {
        val featureVector = betFeatureService.toFeatureVector(toRealizedBet(drawSettledBet))
        assertEquals(Side.BACK, featureVector.side)
        assertTrue(featureVector.features.isNotEmpty())
    }
}