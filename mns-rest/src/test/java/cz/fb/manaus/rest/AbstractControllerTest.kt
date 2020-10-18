package cz.fb.manaus.rest

import cz.fb.manaus.core.batch.SettledBetLoader
import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

@ExperimentalTime
@WebAppConfiguration
abstract class AbstractControllerTest : AbstractIntegrationTestCase() {
    protected lateinit var mvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var settledBetLoader: SettledBetLoader

    protected fun checkResponse(url: String, vararg substrings: String) {
        val result = mvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
        val content = result.response.contentAsString
        substrings.forEach {
            assertTrue { it in content }
        }
    }

    @Before
    fun mockRest() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
        settledBetLoader.invalidateCache()
    }

    protected fun createLiveMarket(): RealizedBet {
        marketRepository.save(market)
        val actionId = betActionRepository.save(betAction).id
        settledBetRepository.save(homeSettledBet)
        return RealizedBet(homeSettledBet, betAction.copy(id = actionId), market)
    }
}
