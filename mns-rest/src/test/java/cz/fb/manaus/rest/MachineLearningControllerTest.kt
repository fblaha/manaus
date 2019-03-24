package cz.fb.manaus.rest

import org.junit.Test


class MachineLearningControllerTest : AbstractControllerTest() {

    @Test
    fun `bet features`() {
        createLiveMarket()
        checkResponse("/ml/bet-features/1d", "features", "price", "reciprocal")
    }

}