package cz.fb.manaus.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.model.MarketFootprint
import cz.fb.manaus.core.model.market
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime


@ExperimentalTime
class MarketFootprintControllerTest : AbstractControllerTest() {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `get by ID`() {
        createLiveMarket()
        checkResponse("/footprints/2", "Banik", "Sparta")
    }

    @Test
    fun `get by ID - missing`() {
        mvc.perform(
                get("/footprints/123456")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound)
                .andReturn()
    }

    @Test
    fun import() {
        val footprint = MarketFootprint(market, emptyList(), emptyList())
        val market = objectMapper.writer().writeValueAsString(footprint)
        val result = mvc.perform(
                MockMvcRequestBuilders.post("/footprints")
                        .content(market)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
    }
}