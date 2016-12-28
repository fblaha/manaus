package cz.fb.manaus.betfair.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import java.util.Date;

public class Event extends EventType {
    private String countryCode;
    private String timezone;
    private String venue;
    @JsonDeserialize(using = DateDeserializer.class)
    private Date openDate;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("countryCode", countryCode)
                .add("timezone", timezone)
                .add("venue", venue)
                .add("openDate", openDate)
                .toString();
    }
}
