package cz.fb.manaus.reactor.categorizer;


import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PlacedBeforeCategorizer extends AbstractBeforeCategorizer {

    public static final String CATEGORY = "placedBefore";
    public static final String MIN_PREFIX = CATEGORY + MIN;

    public PlacedBeforeCategorizer() {
        super(CATEGORY);
    }

    @Override
    protected Date getDate(SettledBet settledBet) {
        return settledBet.getPlaced();
    }

}
