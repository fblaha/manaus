package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String date = jsonParser.getText();
        return deserialize(date);
    }

    Date deserialize(String date) {
        return Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(date)));
    }
}