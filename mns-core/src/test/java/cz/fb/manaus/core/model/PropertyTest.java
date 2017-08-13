package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PropertyTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        Instant expiryDate = Instant.now().plus(20, ChronoUnit.DAYS);
        Property original = new Property("test_name", "test_val", Date.from(expiryDate));
        String serialized = mapper.writer().writeValueAsString(original);
        Property restored = mapper.readerFor(Property.class).readValue(serialized);
        String doubleSerialized = mapper.writer().writeValueAsString(restored);

        assertThat(serialized, is(doubleSerialized));
        assertThat(restored.getName(), is(original.getName()));
        assertThat(restored.getValue(), is(original.getValue()));
        assertThat(restored.getExpiryDate(), is(original.getExpiryDate()));
    }
}