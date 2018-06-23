package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class RunnerPricesTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static RunnerPrices create(long selectionId, Collection<Price> prices, Double matched, Double lastMatchedPrice) {
        var rp = new RunnerPrices();
        rp.setSelectionId(selectionId);
        rp.setPrices(prices);
        rp.setMatchedAmount(matched);
        rp.setLastMatchedPrice(lastMatchedPrice);
        return rp;
    }

    @Test
    public void testMarshallJson() throws Exception {
        RunnerPrices prices = create(111, List.of(), 100d, 5d);
        String json = MAPPER.writeValueAsString(prices);
        assertThat(json, containsString("111"));
    }
}