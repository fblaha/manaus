package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.size;
import static cz.fb.manaus.core.test.CoreTestFactory.DRAW;
import static cz.fb.manaus.core.test.CoreTestFactory.MARKET_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BetUtilsTest extends AbstractLocalTestCase {

    @Autowired
    private BetUtils betUtils;

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
        assertThat(size(filtered), is(1));
        assertThat(getLast(filtered), is(back3));

        filtered = betUtils.getCurrentActions(Arrays.asList(lay1, lay2));
        assertThat(size(filtered), is(2));
        assertThat(getFirst(filtered, null), is(lay1));
        assertThat(getLast(filtered, null), is(lay2));

        filtered = betUtils.getCurrentActions(Arrays.asList(lay1, lay2, lay3));
        assertThat(size(filtered), is(1));
        assertThat(getLast(filtered), is(lay3));
    }

    @Test
    public void testUnknownBets() throws Exception {
        BetAction action = mock(BetAction.class);
        when(action.getBetId()).thenReturn("1", "2");
        Bet bet = mock(Bet.class);
        when(bet.getBetId()).thenReturn("1");
        assertThat(betUtils.getUnknownBets(Collections.singletonList(bet), Collections.singleton("1")).size(), is(0));
        assertThat(betUtils.getUnknownBets(Collections.singletonList(bet), Collections.singleton("2")).size(), is(1));
    }

    @Test
    public void testFindAction() throws Exception {
        Price price = new Price(3d, 30d, Side.BACK);
        BetAction place = new BetAction(BetActionType.UPDATE, DateUtils.addMinutes(new Date(), -120), price, null, DRAW);
        BetAction update = new BetAction(BetActionType.UPDATE, DateUtils.addMinutes(new Date(), -60), price, null, DRAW);

        SettledBet settledBet = new SettledBet(DRAW, "Draw", 5d, new Date(), new Date(), price);
        assertThat(betUtils.findBestMatchingAction(settledBet, 0.001, Arrays.asList(place, update)).get(), sameInstance(update));
        assertThat(betUtils.findBestMatchingAction(settledBet, 0.001, Arrays.asList(place)).get(), sameInstance(place));
    }

    @Test
    public void testFilterDuplicates() throws Exception {
        Bet predecessor = new Bet("1", MARKET_ID, DRAW, new Price(2d, 2d, Side.LAY), new Date(), 1d);
        Bet successor = new Bet("1", MARKET_ID, DRAW, new Price(2d, 2d, Side.LAY), new Date(), 1.5d);
        List<Bet> bets = betUtils.filterDuplicates(Arrays.asList(predecessor, successor, predecessor, successor));

        assertThat(bets.size(), is(1));
        assertThat(bets.get(0), is(successor));
    }

}
