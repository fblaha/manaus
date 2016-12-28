package cz.fb.manaus.betfair.rest;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DateDeserializerTest {

    public static final String DATE = "2014-07-28T13:00:00.111Z";

    @Test
    public void testParseDate() throws Exception {
        Date deserialized = new DateDeserializer().deserialize(DATE);
        assertThat(deserialized, notNullValue());
        assertThat(new DateSerializer().serialize(deserialized), is(DATE));
    }
}