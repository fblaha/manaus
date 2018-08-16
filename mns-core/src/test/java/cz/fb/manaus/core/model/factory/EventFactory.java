package cz.fb.manaus.core.model.factory;

import cz.fb.manaus.core.model.Event;

import java.util.Date;

public class EventFactory {
    public static Event create(String id, String name, Date openDate, String countryCode) {
        var event = new Event();
        event.setId(id);
        event.setName(name);
        event.setOpenDate(openDate);
        event.setCountryCode(countryCode);
        return event;
    }
}
