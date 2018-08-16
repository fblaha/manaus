package cz.fb.manaus.core.model.factory;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;

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
