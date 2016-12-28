package cz.fb.manaus.matchbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.matchbook.rest.Event;
import cz.fb.manaus.matchbook.rest.EventPage;
import cz.fb.manaus.matchbook.rest.Market;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.net.URL;
import java.util.List;

import static cz.fb.manaus.spring.CoreLocalConfiguration.TEST_PROFILE;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class ModelConverterTest extends AbstractLocalTestCase {

    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ModelConverter modelConverter;

    @Test
    public void testToModel() throws Exception {
        URL resource = Resources.getResource(this.getClass(), "rest/events.json");
        EventPage values = mapper.readValue(resource.openStream(), EventPage.class);
        List<Event> events = values.getEvents();
        for (Event event : events) {
            for (Market market : event.getMarkets()) {
                System.out.println(modelConverter.toModel(event, market));
            }
        }
    }


}