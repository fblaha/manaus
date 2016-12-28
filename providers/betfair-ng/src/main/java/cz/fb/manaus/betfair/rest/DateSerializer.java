package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {


    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeRawValue("\"" + serialize(value) + "\"");
    }

    String serialize(Date value) {
        return DateTimeFormatter.ISO_INSTANT.format(value.toInstant());
    }
}