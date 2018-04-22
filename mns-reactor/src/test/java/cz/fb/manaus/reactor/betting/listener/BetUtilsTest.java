package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BetUtilsTest extends AbstractLocalTestCase {

    @Autowired
    private BetUtils betUtils;
    private SettledBet bet;


    @Before
    public void setUp() throws Exception {
        Price price = new Price(5d, 3d, Side.BACK);
        bet = new SettledBet(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5d, new Date(), price);

        BetAction action = new BetAction(BetActionType.PLACE, new Date(), price, null, 1000);
        bet.setBetAction(action);
    }

    @Test
    public void testCurrentActions() throws Exception {
        Date currDate = new Date();
        Price priceBack = new Price(2d, 2d, Side.BACK);
        Price priceLay = new Price(1.8d, 2d, Side.LAY);
        int selectionId = 1;
        BetAction back1 = new BetAction(BetActionType.PLACE, DateUtils.addHours(currDate, -10), priceBack,
                mock(Market.class), selectionId);
        BetAction back2 = new BetAction(BetActionType.PLACE, DateUtils.addHours(currDate, -9), priceBack,
                mock(Market.class), selectionId);
        BetAction back3 = new BetAction(BetActionType.PLACE, DateUtils.addHours(currDate, -8), priceBack,
                mock(Market.class), selectionId);
        BetAction lay1 = new BetAction(BetActionType.PLACE, DateUtils.addHours(currDate, -6), priceLay,
                mock(Market.class), selectionId);
        BetAction lay2 = new BetAction(BetActionType.UPDATE, DateUtils.addHours(currDate, -5), priceLay,
                mock(Market.class), selectionId);
        BetAction lay3 = new BetAction(BetActionType.PLACE, DateUtils.addHours(currDate, -4), priceLay,
                mock(Market.class), selectionId);
        List<BetAction> filtered = betUtils.getCurrentActions(Arrays.asList(back1, back2, back3));
        assertThat(filtered.size(), is(1));
        assertThat(filtered.get(filtered.size() - 1), is(back3));

        filtered = betUtils.getCurrentActions(Arrays.asList(lay1, lay2));
        assertThat(filtered.size(), is(2));
        assertThat(filtered.get(0), is(lay1));
        assertThat(filtered.get(filtered.size() - 1), is(lay2));

        filtered = betUtils.getCurrentActions(Arrays.asList(lay1, lay2, lay3));
        assertThat(filtered.size(), is(1));
        assertThat(filtered.get(filtered.size() - 1), is(lay3));
    }

    @Test
    public void testUnknownBets() throws Exception {
        BetAction action = mock(BetAction.class);
        when(action.getBetId()).thenReturn("1", "2");
        Bet bet = mock(Bet.class);
        when(bet.getBetId()).thenReturn("1");
        assertThat(betUtils.getUnknownBets(List.of(bet), Collections.singleton("1")).size(), is(0));
        assertThat(betUtils.getUnknownBets(List.of(bet), Collections.singleton("2")).size(), is(1));
    }

    @Test
    public void testCeilAmount() throws Exception {
        SettledBet ceilCopy = betUtils.limitBetAmount(2d, bet);
        assertThat(ceilCopy, not(sameInstance(bet)));
        assertThat(ceilCopy.getSelectionName(), is(bet.getSelectionName()));
        assertThat(ceilCopy.getSelectionId(), is(bet.getSelectionId()));
        assertThat(ceilCopy.getProfitAndLoss(), closeTo(bet.getProfitAndLoss() * 2d / 3, 0.001d));
    }

    @Test
    public void testCeilActionAmount() throws Exception {
        SettledBet ceilCopy = betUtils.limitBetAmount(2d, bet);
        BetAction action = bet.getBetAction();
        BetAction actionCopy = ceilCopy.getBetAction();
        assertThat(action, not(sameInstance(actionCopy)));
        assertThat(action.getActionDate(), is(actionCopy.getActionDate()));
        assertThat(action.getSelectionId(), is(actionCopy.getSelectionId()));
        assertThat(actionCopy.getPrice().getAmount(), is(2d));
    }

    @Test
    public void testHighCeiling() throws Exception {
        SettledBet ceilCopy = betUtils.limitBetAmount(100d, bet);
        BetAction action = bet.getBetAction();
        BetAction actionCopy = ceilCopy.getBetAction();

        assertThat(ceilCopy, sameInstance(bet));
        assertThat(ceilCopy.getPrice(), sameInstance(bet.getPrice()));
        assertThat(actionCopy, sameInstance(action));
        assertThat(actionCopy.getPrice(), sameInstance(action.getPrice()));
    }
}
