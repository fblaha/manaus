package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.homeSettledBet
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ContextConfiguration(classes = [SettledBetController::class])
class SettledBetControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setUp() {
        createLiveMarket()
    }

    @Test
    fun `bet list`() {
        checkResponse("/bets", "settled")
        checkResponse("/bets/2d", "settled")
    }

    @Test
    fun `post settled bet`() {
        val original = homeSettledBet
        val serialized = objectMapper.writer().writeValueAsString(original)
        checkPost(serialized, 202)
    }

    private fun checkPost(serialized: String, expectedStatus: Int) {
        mvc.perform(post("/bets")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialized))
                .andExpect(status().`is`(expectedStatus))
                .andReturn()
    }
}