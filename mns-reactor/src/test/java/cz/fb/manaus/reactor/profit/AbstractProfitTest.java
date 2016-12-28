package cz.fb.manaus.reactor.profit;

import cz.fb.manaus.core.model.EventType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.time.DateUtils.addDays;

public abstract class AbstractProfitTest extends AbstractLocalTestCase {
    protected Date marketDate;
    private Market market;

    @Before
    public void setUp() throws Exception {
        marketDate = addDays(DateUtils.truncate(new Date(), Calendar.MONTH), -10);
        market = CoreTestFactory.newMarket("1", marketDate, CoreTestFactory.MATCH_ODDS);
        market.setEventType(new EventType("1", "soccer"));
    }

    protected void setBetAction(SettledBet... bets) {
        for (int i = 0; i < bets.length; i++) {
            SettledBet bet = bets[i];
            bet.setBetAction(CoreTestFactory.newBetAction(Integer.toString(i + 1), market));
        }
    }

    protected List<SettledBet> generateBets(Optional<Side> requestedSide) {
        List<SettledBet> result = new LinkedList<>();
        for (double price = 1.5d; price < 4; price += 0.02) {
            addSideBets(result, price, Side.LAY, requestedSide);
            addSideBets(result, price + 0.1, Side.BACK, requestedSide);
        }
        setBetAction(result.toArray(new SettledBet[result.size()]));
        return result;
    }

    private void addSideBets(List<SettledBet> result, double price, Side side, Optional<Side> requestedSide) {
        if (requestedSide.orElse(side) == side) {
            result.add(new SettledBet(CoreTestFactory.DRAW, "The Draw", 5d, marketDate, marketDate,
                    new Price(price, 4d, side)));
            result.add(new SettledBet(CoreTestFactory.HOME, "Home", 5d, marketDate, marketDate,
                    new Price(price, 4d, side)));
        }
    }

}
