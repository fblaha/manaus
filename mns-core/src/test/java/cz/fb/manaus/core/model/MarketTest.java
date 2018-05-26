package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MarketTest {

    @Test
    public void testDoubleSerialization() throws Exception {
        var market = CoreTestFactory.newMarket();
        var mapper = new ObjectMapper();
        var serialized = mapper.writer().writeValueAsString(market);
        market = mapper.readerFor(Market.class).readValue(serialized);
        var doubleSerialized = mapper.writer().writeValueAsString(market);
        assertEquals(serialized, doubleSerialized);
    }

}
