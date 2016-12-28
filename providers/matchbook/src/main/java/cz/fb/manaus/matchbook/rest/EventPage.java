package cz.fb.manaus.matchbook.rest;

import com.google.common.base.MoreObjects;

import java.util.List;

public class EventPage extends AbstractPage {
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("offset", getOffset())
                .add("total", getTotal())
                .add("perPage", getPerPage())
                .add("events", getEvents())
                .toString();
    }
}
