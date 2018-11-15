package cz.fb.manaus.rest

import cz.fb.manaus.core.model.RealizedBet
import cz.fb.manaus.core.model.betAction
import cz.fb.manaus.core.model.homeSettledBet
import cz.fb.manaus.core.model.market
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
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

@WebAppConfiguration
abstract class AbstractControllerTest : AbstractDatabaseTestCase() {
    protected lateinit var mvc: MockMvc
    @Autowired
    private lateinit var context: WebApplicationContext

    protected fun checkResponse(url: String, vararg substrings: String) {
        val result = mvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
        val content = result.response.contentAsString
        for (substring in substrings) {
            assertTrue(substring in content)
        }
    }

    @Before
    fun mockRest() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    protected fun createLiveMarket(): RealizedBet {
        marketRepository.saveOrUpdate(market)
        val actionID = betActionRepository.save(betAction)
        settledBetRepository.save(homeSettledBet)
        return RealizedBet(homeSettledBet, betAction.copy(id = actionID), market)
    }
}
