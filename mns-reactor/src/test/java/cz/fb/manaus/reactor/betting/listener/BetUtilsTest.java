package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionTest;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.SettledBetTest;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
    public void setUp() {
        var price = new Price(5d, 3d, Side.BACK);
        bet = SettledBetTest.create(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5d, new Date(), price);

        var action = BetActionTest.create(BetActionType.PLACE, new Date(), price, null, 1000);
        bet.setBetAction(action);
    }

    @Test
    public void testCurrentActions() {
        var currDate = new Date();
        var priceBack = new Price(2d, 2d, Side.BACK);
        var priceLay = new Price(1.8d, 2d, Side.LAY);
        var selectionId = 1;
        var back1 = BetActionTest.create(BetActionType.PLACE, DateUtils.addHours(currDate, -10), priceBack,
                mock(Market.class), selectionId);
        var back2 = BetActionTest.create(BetActionType.PLACE, DateUtils.addHours(currDate, -9), priceBack,
                mock(Market.class), selectionId);
        var back3 = BetActionTest.create(BetActionType.PLACE, DateUtils.addHours(currDate, -8), priceBack,
                mock(Market.class), selectionId);
        var lay1 = BetActionTest.create(BetActionType.PLACE, DateUtils.addHours(currDate, -6), priceLay,
                mock(Market.class), selectionId);
        var lay2 = BetActionTest.create(BetActionType.UPDATE, DateUtils.addHours(currDate, -5), priceLay,
                mock(Market.class), selectionId);
        var lay3 = BetActionTest.create(BetActionType.PLACE, DateUtils.addHours(currDate, -4), priceLay,
                mock(Market.class), selectionId);
        var filtered = betUtils.getCurrentActions(List.of(back1, back2, back3));
        assertThat(filtered.size(), is(1));
        assertThat(filtered.get(filtered.size() - 1), is(back3));

        filtered = betUtils.getCurrentActions(List.of(lay1, lay2));
        assertThat(filtered.size(), is(2));
        assertThat(filtered.get(0), is(lay1));
        assertThat(filtered.get(filtered.size() - 1), is(lay2));

        filtered = betUtils.getCurrentActions(List.of(lay1, lay2, lay3));
        assertThat(filtered.size(), is(1));
        assertThat(filtered.get(filtered.size() - 1), is(lay3));
    }

    @Test
    public void testUnknownBets() {
        var action = mock(BetAction.class);
        when(action.getBetId()).thenReturn("1", "2");
        var bet = mock(Bet.class);
        when(bet.getBetId()).thenReturn("1");
        assertThat(betUtils.getUnknownBets(List.of(bet), Set.of("1")).size(), is(0));
        assertThat(betUtils.getUnknownBets(List.of(bet), Set.of("2")).size(), is(1));
    }

    @Test
    public void testCeilAmount() {
        var ceilCopy = betUtils.limitBetAmount(2d, bet);
        assertThat(ceilCopy, not(sameInstance(bet)));
        assertThat(ceilCopy.getSelectionName(), is(bet.getSelectionName()));
        assertThat(ceilCopy.getSelectionId(), is(bet.getSelectionId()));
        assertThat(ceilCopy.getProfitAndLoss(), closeTo(bet.getProfitAndLoss() * 2d / 3, 0.001d));
    }

    @Test
    public void testCeilActionAmount() {
        var ceilCopy = betUtils.limitBetAmount(2d, bet);
        var action = bet.getBetAction();
        var actionCopy = ceilCopy.getBetAction();
        assertThat(action, not(sameInstance(actionCopy)));
        assertThat(action.getActionDate(), is(actionCopy.getActionDate()));
        assertThat(action.getSelectionId(), is(actionCopy.getSelectionId()));
        assertThat(actionCopy.getPrice().getAmount(), is(2d));
    }

    @Test
    public void testHighCeiling() {
        var ceilCopy = betUtils.limitBetAmount(100d, bet);
        var action = bet.getBetAction();
        var actionCopy = ceilCopy.getBetAction();

        assertThat(ceilCopy, sameInstance(bet));
        assertThat(ceilCopy.getPrice(), sameInstance(bet.getPrice()));
        assertThat(actionCopy, sameInstance(action));
        assertThat(actionCopy.getPrice(), sameInstance(action.getPrice()));
    }
}
