package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SettledPageTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserializeNG() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "settlements-ng.json");
        SettledPage values = mapper.readValue(resource.openStream(), SettledPage.class);
        System.out.println("values = " + values);
        assertThat(values.getEvents().size(), is(2));
        assertThat(values.getEvents().get(0), instanceOf(SettledEvent.class));
    }

}