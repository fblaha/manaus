package cz.fb.manaus.core.model;

import java.util.Collection;
import java.util.Date;

public interface ModelFactory {
    static BetAction newAction(BetActionType betActionType, Date actionDate, Price price,
                               Market market, long selectionId) {
        var ba = new BetAction();
        ba.setBetActionType(betActionType);
        ba.setActionDate(actionDate);
        ba.setPrice(price);
        ba.setMarket(market);
        ba.setSelectionId(selectionId);
        return ba;
    }

    static Event newEvent(String id, String name, Date openDate, String countryCode) {
        var event = new Event();
        event.setId(id);
        event.setName(name);
        event.setOpenDate(openDate);
        event.setCountryCode(countryCode);
        return event;
    }

    static MarketPrices newPrices(int winnerCount, Market market, Collection<RunnerPrices> runnerPrices, Date time) {
        var mp = new MarketPrices();
        mp.setWinnerCount(winnerCount);
        mp.setMarket(market);
        mp.setRunnerPrices(runnerPrices);
        mp.setTime(time);
        return mp;
    }

    static Runner newRunner(long selectionId, String name, double handicap, int sortPriority) {
        var runner = new Runner();
        runner.setSelectionId(selectionId);
        runner.setName(name);
        runner.setHandicap(handicap);
        runner.setSortPriority(sortPriority);
        return runner;
    }

    static RunnerPrices newRunnerPrices(long selectionId, Collection<Price> prices, Double matched, Double lastMatchedPrice) {
        var rp = new RunnerPrices();
        rp.setSelectionId(selectionId);
        rp.setPrices(prices);
        rp.setMatchedAmount(matched);
        rp.setLastMatchedPrice(lastMatchedPrice);
        return rp;
    }

    static SettledBet newSettled(long selectionId, String selectionName, double profitAndLoss, Date settled, Price price) {
        var bet = new SettledBet();
        bet.setSelectionId(selectionId);
        bet.setSelectionName(selectionName);
        bet.setProfitAndLoss(profitAndLoss);
        bet.setSettled(settled);
        bet.setPrice(price);
        return bet;
    }
}
