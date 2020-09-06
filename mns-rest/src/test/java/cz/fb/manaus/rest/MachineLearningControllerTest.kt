package cz.fb.manaus.rest

import org.junit.Test
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MachineLearningControllerTest : AbstractControllerTest() {

    @Test
    fun `bet features`() {
        createLiveMarket()
        checkResponse("/ml/bet-features/1d", "features", "price", "reciprocal")
    }

}