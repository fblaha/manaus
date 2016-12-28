package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParamsTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        Filter filter = new Filter();
        filter.setEventIds(Collections.singleton("156"));
        Params params = Params.withFilter(filter);
        params.setMaxResults(200);
        params.setMarketProjection(EnumSet.allOf(MarketProjection.class));
        String val = mapper.writeValueAsString(params);
        assertThat(val, containsString("156"));
        System.out.println("val = " + val);
    }
}