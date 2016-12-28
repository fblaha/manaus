package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class OfferTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialize() throws Exception {
        Offer offer = new Offer();
        offer.setSide("lay");
        offer.setOdds(3.25);
        offer.setStake(2);
        offer.setTempId(2);
        String serialized = mapper.writeValueAsString(offer);
        JsonNode node = mapper.readTree(serialized);
        assertThat(node.get("id"), nullValue());
    }


}