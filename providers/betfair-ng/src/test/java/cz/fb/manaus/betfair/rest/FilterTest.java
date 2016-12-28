package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class FilterTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        Filter filter = new Filter();
        filter.setEventTypeIds(Collections.singleton("1"));
        TimeRange marketStartTime = new TimeRange();
        marketStartTime.setFrom(new Date());
        marketStartTime.setTo(new Date());
        filter.setMarketStartTime(marketStartTime);
        String json = mapper.writeValueAsString(Params.withFilter(filter));
        System.out.println(json);
        assertThat(json, containsString("marketStartTime"));
    }

}