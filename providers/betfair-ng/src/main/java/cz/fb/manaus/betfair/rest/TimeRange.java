package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class TimeRange {
    @JsonSerialize(using = DateSerializer.class)
    private Date from;
    @JsonSerialize(using = DateSerializer.class)
    private Date to;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .toString();
    }
}
