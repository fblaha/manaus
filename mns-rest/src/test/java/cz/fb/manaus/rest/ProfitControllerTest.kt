package cz.fb.manaus.rest

import org.junit.Test


class ProfitControllerTest : AbstractControllerTest() {

    @Test
    fun `profit records`() {
        createLiveMarket()
        checkResponse("/profit/1d", "category", "side_back", "profit")
    }

    @Test
    fun `progress records`() {
        createLiveMarket()
        checkResponse("/fc-progress/1d", "category", "actualMarketMatched", "actualRunnerMatched", "fairnessBack")
    }

    @Test
    fun `progress single function`() {
        createLiveMarket()
        checkResponse("/fc-progress/1d?function=actualMarketMatched", "actualMarketMatched")
    }

}