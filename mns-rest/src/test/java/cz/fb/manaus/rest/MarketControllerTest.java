package cz.fb.manaus.rest;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.net.HttpHeaders;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MarketControllerTest extends AbstractControllerTest {

    @Autowired
    private MetricRegistry metricRegistry;

    @Test
    public void testMarketList() throws Exception {
        createMarketWithSingleAction();
        checkResponse("/markets", CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
        checkResponse("/markets/" + MARKET_ID, CoreTestFactory.EVENT_NAME, CoreTestFactory.DRAW_NAME);
    }

    @Test
    public void testMarketCreate() throws Exception {
        var market = new ObjectMapper().writer().writeValueAsString(CoreTestFactory.newMarket());
        var result = mvc.perform(post("/markets")
                .content(market)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION), notNullValue());
    }

    @Test(expected = NullPointerException.class)
    public void tesMissingID() throws Throwable {
        var market = CoreTestFactory.newMarket();
        var originalExceptionCount = getExceptionCount();
        market.setId(null);
        var payload = new ObjectMapper().writer().writeValueAsString(market);
        try {
            mvc.perform(post("/markets")
                    .content(payload)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        } catch (NestedServletException e) {
            assertThat(getExceptionCount(), is(originalExceptionCount + 1));
            throw Throwables.getRootCause(e);
        }
    }

    public long getExceptionCount() {
        return metricRegistry.counter(ExceptionCounter.METRIC_NAME).getCount();
    }
}