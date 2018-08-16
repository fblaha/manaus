package cz.fb.manaus.core.model.factory;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;

import java.util.Date;

public class SettledBetFactory {
    public static SettledBet create(long selectionId, String selectionName, double profitAndLoss, Date settled, Price price) {
        var bet = new SettledBet();
        bet.setSelectionId(selectionId);
        bet.setSelectionName(selectionName);
        bet.setProfitAndLoss(profitAndLoss);
        bet.setSettled(settled);
        bet.setPrice(price);
        return bet;
    }
}
