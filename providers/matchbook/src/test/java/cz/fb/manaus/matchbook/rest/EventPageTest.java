package cz.fb.manaus.matchbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EventPageTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserialize() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "events.json");
        EventPage values = mapper.readValue(resource.openStream(), EventPage.class);
        System.out.println("values = " + values);
        assertThat(values.getEvents().size(), is(10));
        assertThat(values.getEvents().get(0), instanceOf(Event.class));
    }

    @Test
    public void testTimeDeserialize() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "events.json");
        EventPage values = mapper.readValue(resource.openStream(), EventPage.class);
        List<Event> events = values.getEvents();
        checkStartTime(events, "spurs", 2, 5);
        checkStartTime(events, "radwanska", 3, 35);
    }

    private void checkStartTime(List<Event> events, String name, int hours, int min) throws java.io.IOException {
        Optional<Event> spurs = events.stream().filter(e -> e.getName().toLowerCase().contains(name)).findAny();
        Instant start = spurs.get().getStart().toInstant();
        ZoneId prague = ZoneId.of("Europe/Prague");
        ZonedDateTime pragueStart = start.atZone(prague);
        assertThat(pragueStart.getHour(), is(hours));
        assertThat(pragueStart.getMinute(), is(min));
    }

}