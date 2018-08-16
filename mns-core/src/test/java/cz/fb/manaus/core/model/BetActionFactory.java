package cz.fb.manaus.core.model;

import java.util.Date;

public class BetActionFactory {
    public static BetAction create(BetActionType betActionType, Date actionDate, Price price,
                                   Market market, long selectionId) {
        var ba = new BetAction();
        ba.setBetActionType(betActionType);
        ba.setActionDate(actionDate);
        ba.setPrice(price);
        ba.setMarket(market);
        ba.setSelectionId(selectionId);
        return ba;
    }
}
