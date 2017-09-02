package cz.fb.manaus.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CollectedBets {
    private final List<Bet> place;
    private final List<Bet> update;
    private final List<String> cancel;

    private CollectedBets(List<Bet> place, List<Bet> update, List<String> cancel) {
        this.place = place;
        this.update = update;
        this.cancel = cancel;
    }

    public static CollectedBets create() {
        return new CollectedBets(new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
    }

    public static CollectedBets empty() {
        return new CollectedBets(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public List<Bet> getPlace() {
        return place;
    }

    public List<Bet> getUpdate() {
        return update;
    }

    public List<String> getCancel() {
        return cancel;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return place.isEmpty() && update.isEmpty() && cancel.isEmpty();
    }
}
