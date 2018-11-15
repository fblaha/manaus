package cz.fb.manaus.rest

import org.junit.Test
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [ProfitController::class])
class ProfitControllerTest : AbstractControllerTest() {

    @Test
    fun `profit records`() {
        createLiveMarket()
        checkResponse("/profit/1d", "category", "side_back", "profit")
    }

    @Test
    fun `progress records`() {
        createLiveMarket()
        checkResponse("/fc-progress/1d", "category", "actualMatched", "actualMatched", "fairnessBack")
    }

    @Test
    fun `coverage records`() {
        createLiveMarket()
        checkResponse("/fc-coverage/1d", "placedAhead_solo")
    }

    @Test
    fun `progress single function`() {
        createLiveMarket()
        checkResponse("/fc-progress/1d?function=actualMatched", "actualMatched")
    }

}