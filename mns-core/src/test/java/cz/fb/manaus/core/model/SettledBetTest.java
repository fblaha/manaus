package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SettledBetTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerialization() throws Exception {
        var original = SettledBet.create(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5d, new Date(), new Price(5d, 3d, Side.BACK));

        var serialized = mapper.writer().writeValueAsString(original);
        SettledBet restored = mapper.readerFor(SettledBet.class).readValue(serialized);
        assertThat(restored.getPrice(), is(original.getPrice()));
        assertThat(restored.getSettled(), is(original.getSettled()));
    }

}