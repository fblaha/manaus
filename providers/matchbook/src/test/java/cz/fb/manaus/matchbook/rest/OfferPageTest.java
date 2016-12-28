package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OfferPageTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserialize() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "offers.json");
        OfferPage values = mapper.readValue(resource.openStream(), OfferPage.class);
        System.out.println("values = " + values);
        assertThat(values.getOffers().size(), is(3));
        assertThat(values.getOffers().get(0), instanceOf(Offer.class));
    }
}