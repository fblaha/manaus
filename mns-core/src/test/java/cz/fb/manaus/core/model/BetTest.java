package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BetTest extends AbstractLocalTestCase {

    @Autowired
    private ExchangeProvider provider;

    @Test
    public void testHalfMatched() throws Exception {
        assertTrue(createBet(provider.getMinAmount()).isHalfMatched());
        assertTrue(createBet(1.5).isHalfMatched());
        assertFalse(createBet(0d).isHalfMatched());
        assertFalse(createBet(0.8d).isHalfMatched());
    }

    @Test
    public void testMatched() throws Exception {
        assertTrue(createBet(provider.getMinAmount()).isMatched());
        assertTrue(createBet(1.5).isMatched());
        assertFalse(createBet(0d).isMatched());
        assertTrue(createBet(0.8d).isMatched());
    }

    @Test
    public void testSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Bet original = new Bet("111", "222", 333,
                new Price(3d, 2d, Side.BACK), new Date(), 0d);

        String serialized = mapper.writer().writeValueAsString(original);
        Bet restored = mapper.readerFor(Bet.class).readValue(serialized);
        assertThat(restored.getRequestedPrice(), is(original.getRequestedPrice()));
        assertThat(restored.getPlacedDate(), is(original.getPlacedDate()));
        String doubleSerialized = mapper.writer().writeValueAsString(restored);
        assertEquals(serialized, doubleSerialized);
    }

    private Bet createBet(double matchedAmount) {
        String marketId = CoreTestFactory.MARKET_ID;
        long selectionId = CoreTestFactory.DRAW;
        Price requestedPrice = new Price(3d, provider.getMinAmount(), Side.LAY);
        Instant date = Instant.now().minus(2, ChronoUnit.HOURS);
        return new Bet("1", marketId, selectionId, requestedPrice, Date.from(date), matchedAmount);
    }
}