package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SettlementPageTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserialize() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "settlements.json");
        SettlementPage values = mapper.readValue(resource.openStream(), SettlementPage.class);
        System.out.println("values = " + values);
        assertThat(values.getMarkets().size(), is(3));
        assertThat(values.getMarkets().get(0), instanceOf(Settlement.class));
    }
}