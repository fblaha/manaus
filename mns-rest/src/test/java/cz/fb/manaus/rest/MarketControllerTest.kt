package cz.fb.manaus.rest

import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import com.google.common.net.HttpHeaders
import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.test.CoreTestFactory
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.NestedServletException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class MarketControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var metricRegistry: MetricRegistry

    private val exceptionCount: Long
        get() = metricRegistry.counter("_ERROR_").count

    @Test
    fun `market list`() {
        createMarketWithSingleAction()
        checkResponse("/markets", CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME)
        checkResponse("/markets/" + AbstractDaoTest.MARKET_ID, CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME)
    }

    @Test
    fun `market create`() {
        val market = ObjectMapper().writer().writeValueAsString(CoreTestFactory.newTestMarket())
        val result = mvc.perform(post("/markets")
                .content(market)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated)
                .andReturn()
        assertNotNull(result.response.getHeader(HttpHeaders.LOCATION))
    }

    @Test(expected = NullPointerException::class)
    fun `missing id`() {
        val market = CoreTestFactory.newTestMarket()
        val originalExceptionCount = exceptionCount
        market.id = null
        val payload = ObjectMapper().writer().writeValueAsString(market)
        try {
            mvc.perform(post("/markets")
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
        } catch (e: NestedServletException) {
            assertEquals(originalExceptionCount + 1, exceptionCount)
            throw Throwables.getRootCause(e)
        }

    }
}