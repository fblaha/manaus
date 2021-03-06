package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.model.market
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MarketControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `market list`() {
        createLiveMarket()
        checkResponse("/markets", "Banik", "Sparta")
    }

    @Test
    fun `market ID list`() {
        createLiveMarket()
        checkResponse("/market-ids", "2")
    }

    @Test
    fun `market create`() {
        val market = objectMapper.writer().writeValueAsString(market)
        val result = mvc.perform(
                post("/markets")
                        .content(market)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
    }

}