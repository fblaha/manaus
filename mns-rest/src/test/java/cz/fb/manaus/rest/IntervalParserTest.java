package cz.fb.manaus.rest;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntervalParserTest extends AbstractLocalTestCase {

    @Autowired
    private IntervalParser intervalParser;

    @Test
    public void testSubtract() throws Exception {
        Instant now = Instant.now();
        assertThat(intervalParser.parse(now, "2h").lowerEndpoint(), is(now.minus(2, ChronoUnit.HOURS)));
        assertThat(intervalParser.parse(now, "10d").lowerEndpoint(), is(now.minus(10, ChronoUnit.DAYS)));
        assertThat(intervalParser.parse(now, "5m").lowerEndpoint(), is(now.minus(5, ChronoUnit.MINUTES)));
    }

    @Test
    public void testOffsetLowerEndpoint() throws Exception {
        Instant now = Instant.now();
        assertThat(intervalParser.parse(now, "2h-4").lowerEndpoint(), is(now.minus(6, ChronoUnit.HOURS)));
        assertThat(intervalParser.parse(now, "10d-2").lowerEndpoint(), is(now.minus(12, ChronoUnit.DAYS)));
    }

    @Test
    public void testOffsetUpperEndpoint() throws Exception {
        Instant now = Instant.now();
        assertThat(intervalParser.parse(now, "2h-4").upperEndpoint(), is(now.minus(4, ChronoUnit.HOURS)));
        assertThat(intervalParser.parse(now, "10d-2").upperEndpoint(), is(now.minus(2, ChronoUnit.DAYS)));
    }

}
