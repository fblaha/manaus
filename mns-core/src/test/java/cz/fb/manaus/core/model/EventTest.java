package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class EventTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static Event create(String id, String name, Date openDate, String countryCode) {
        var event = new Event();
        event.setId(id);
        event.setName(name);
        event.setOpenDate(openDate);
        event.setCountryCode(countryCode);
        return event;
    }

    @Test
    public void testJsonMarshall() throws Exception {
        Event event = create("100", "Sparta vs Ostrava", new Date(), "cz");
        String json = MAPPER.writeValueAsString(event);
        assertThat(json, containsString("Ostrava"));

    }
}