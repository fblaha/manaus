package cz.fb.manaus.core.dao;

import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static cz.fb.manaus.core.test.CoreTestFactory.newMarket;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BetActionDaoTest extends AbstractDaoTest {

    @Test
    public void testBetIds() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, new Date(), singletonMap("k1", "XXX"), BET_ID);
        Set<String> ids = betActionDao.getBetActionIds(market.getId(), OptionalLong.empty(), empty());
        assertThat(ids, hasItem(BET_ID));
    }

    @Test
    public void testUpdateBetId() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        BetAction action = createAndSaveBetAction(market, new Date(), Collections.emptyMap(), BET_ID);
        String newId = BET_ID + "_1";
        String marketId = action.getMarket().getId();
        assertThat(betActionDao.updateBetId(BET_ID, newId), is(1));
        assertThat(betActionDao.updateBetId(BET_ID, newId), is(0));
        assertTrue(betActionDao.getBetAction(newId).isPresent());
        assertFalse(betActionDao.getBetAction(BET_ID).isPresent());
    }

    @Test
    public void testSetBetId() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        BetAction action = createAndSaveBetAction(market, new Date(), Collections.emptyMap(), null);
        Integer actionId = action.getId();
        assertThat(betActionDao.setBetId(actionId, "111"), is(1));
        assertThat(betActionDao.get(actionId).get().getBetId(), is("111"));
    }

    @Test
    public void testBetAction() {
        Market market = newMarket();
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, new Date(), singletonMap("k1", "XXX"), BET_ID);
        checkCount(market.getId(), OptionalLong.empty(), empty(), 1);
        checkCount(market.getId(), OptionalLong.of(CoreTestFactory.DRAW), empty(), 1);
        checkCount(market.getId(), OptionalLong.of(CoreTestFactory.DRAW + 1), empty(), 0);
        checkCount(market.getId(), OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY), 1);
        checkCount(market.getId(), OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.BACK), 0);
        checkCount(market.getId(), OptionalLong.empty(), Optional.of(Side.LAY), 1);
        checkCount(market.getId(), OptionalLong.empty(), Optional.of(Side.BACK), 0);
    }

    private void checkCount(String marketId, OptionalLong selId, Optional<Side> side, long expectedCount) {
        assertThat(betActionDao.getBetActions(marketId, selId, side).size(), is((int) expectedCount));
        assertThat(betActionDao.getBetActionIds(marketId, selId, side).size(), is((int) expectedCount));
    }

    @Test
    public void testBetActionProperties() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);

        createAndSaveBetAction(market, new Date(), singletonMap("k1", "newer"), BET_ID);
        BetAction action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).get(0);
        betActionDao.getBetActions(OptionalInt.empty());
        assertThat(action.getProperties().size(), is(1));
        assertThat(action.getProperties().get("k1"), is("newer"));
    }


    @Test
    public void testBetActionPropertiesOrder() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, new Date(), singletonMap("k1", "newer"), BET_ID);
        createAndSaveBetAction(market, addHours(new Date(), -1), singletonMap("k1", "older"), BET_ID + 1);
        BetAction action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).get(0);
        assertThat(action.getProperties().size(), is(1));
        assertThat(action.getProperties().get("k1"), is("older"));

        action = betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).get(1);
        assertThat(action.getProperties().size(), is(1));
        assertThat(action.getProperties().get("k1"), is("newer"));
        // delete with properties
        marketDao.delete(market.getId());
    }

    @Test
    public void testBetActionPropertiesDelete() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, addHours(new Date(), -1), singletonMap("k1", "older"), BET_ID);

        assertTrue(betActionDao.getBetAction(BET_ID).isPresent());
        marketDao.delete(market.getId());
        assertFalse(betActionDao.getBetAction(BET_ID).isPresent());
    }

    @Test
    public void testBetActionPropertiesDuplicity() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, new Date(), of("k1", "v1", "k2", "v2"), BET_ID);
        assertThat(betActionDao.getBetActions(OptionalInt.empty()).size(), is(1));
        assertThat(betActionDao.getBetActions("33", OptionalLong.of(CoreTestFactory.DRAW), Optional.of(Side.LAY)).size(), is(1));
    }

    @Test
    public void testMaxResults() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, new Date(), of("k1", "v1", "k2", "v2"), BET_ID);
        createAndSaveBetAction(market, new Date(), of("k1", "v1", "k2", "v2"), BET_ID + 1);
        assertThat(betActionDao.getBetActions(OptionalInt.of(1)).size(), is(1));
        assertThat(betActionDao.getBetActions(OptionalInt.of(2)).size(), is(2));
    }

    @Test
    public void testBetActionByBetId() {
        createMarketWithSingleAction();
        assertTrue(betActionDao.getBetAction(BET_ID).isPresent());
        assertFalse(betActionDao.getBetAction(BET_ID + 1).isPresent());
    }


    @Test
    public void testBetActionDateByBetId() {
        createMarketWithSingleAction();
        assertTrue(betActionDao.getBetActionDate(BET_ID).isPresent());
        assertFalse(betActionDao.getBetActionDate(BET_ID + 1).isPresent());
    }

    @Test
    public void testMarketPrices() {
        createMarketWithSingleAction();
        BetAction betAction = betActionDao.getBetAction(BET_ID).get();
        assertThat(betAction.getMarketPrices(), notNullValue());
    }

    @Test(expected = LazyInitializationException.class)
    public void testMarketPricesLazy() {
        createMarketWithSingleAction();
        BetAction betAction = betActionDao.getBetAction(BET_ID).get();
        betAction.getMarketPrices().getReciprocal(Side.BACK);
    }

    @Test
    public void testMarketPricesLazyFetch() {
        createMarketWithSingleAction();
        BetAction action = betActionDao.getBetAction(BET_ID).get();
        betActionDao.fetchMarketPrices(action);
        assertThat(action.getMarketPrices().getTime(), notNullValue());
        assertThat(action.getMarketPrices().getReciprocal(Side.BACK).getAsDouble(), is(0.8333333333333333d));
        assertEquals(1.2d, action.getMarketPrices().getOverround(Side.BACK).getAsDouble(), 0.0001d);
    }

    @Test(expected = LazyInitializationException.class)
    public void testMarketLazy() {
        createMarketWithSingleAction();
        BetAction action = betActionDao.getBetAction(BET_ID).get();
        action.getMarketPrices().getMarket().getName();
    }

    @Test
    public void testMarketPricesCache() {
        createMarketWithSingleAction();
        getSessionFactory().getStatistics().clear();
        getSessionFactory().getCache().evictAllRegions();
        checkStats(0, 0, 0, 0, MarketPrices.class);
        checkStats(0, 0, 0, 0, RunnerPrices.class);
        BetAction action = betActionDao.getBetAction(BET_ID).get();
        checkStats(0, 0, 0, 0, MarketPrices.class);
        checkStats(0, 0, 0, 0, RunnerPrices.class);
        betActionDao.fetchMarketPrices(action);
        checkStats(1, 0, 1, 1, MarketPrices.class);
        checkStats(3, 0, 0, 3, RunnerPrices.class);
        action = betActionDao.getBetAction(BET_ID).get();
        checkStats(1, 0, 1, 1, MarketPrices.class);
        checkStats(3, 0, 0, 3, RunnerPrices.class);
        betActionDao.fetchMarketPrices(action);
        checkStats(1, 1, 1, 1, MarketPrices.class);
        checkStats(3, 3, 0, 3, RunnerPrices.class);
    }

    @Test
    public void testTags() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, addHours(new Date(), -1), PROPS, BET_ID);
        BetAction betAction = betActionDao.getBetAction(BET_ID).get();
        assertThat(betAction.getTags(), is(TAGS));
    }

    @Test
    public void testRunnerCount() {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        createAndSaveBetAction(market, addHours(new Date(), -1), PROPS, BET_ID);
        BetAction stored = betActionDao.getBetAction(BET_ID).get();
        assertThat(stored.getMarket().getRunners().size(), is(market.getRunners().size()));
    }

    @Test
    public void testBetActionSortAsc() {
        saveActionsAndCheckOrder(comparing(BetAction::getActionDate));
    }

    @Test
    public void testBetActionSortDesc() {
        saveActionsAndCheckOrder(comparing(BetAction::getActionDate).reversed());
    }

    private void saveActionsAndCheckOrder(Comparator<BetAction> comparator) {
        Market market = newMarket("33", new Date(), CoreTestFactory.MATCH_ODDS);
        marketDao.saveOrUpdate(market);
        BetAction betActionEarlier = new BetAction(BetActionType.PLACE, DateUtils.addDays(new Date(), -1), new Price(2d, 30d, Side.LAY), market, CoreTestFactory.DRAW, BET_ID);
        BetAction betActionLater = new BetAction(BetActionType.PLACE, new Date(), new Price(3d, 33d, Side.LAY), market, CoreTestFactory.DRAW, BET_ID + 1);
        List<BetAction> actions = Arrays.asList(betActionLater, betActionEarlier);
        Ordering.from(comparator).immutableSortedCopy(actions).forEach(betActionDao::saveOrUpdate);
        List<BetAction> betActionsForMarket = betActionDao.getBetActions(market.getId(), OptionalLong.empty(), empty());
        assertThat(betActionsForMarket.size(), is(2));
        assertThat(2d, is(betActionsForMarket.get(0).getPrice().getPrice()));
        assertThat(3d, is(betActionsForMarket.get(1).getPrice().getPrice()));
    }

    @Test
    public void testBetActionWithRunnerPrices() {
        Market market = newMarket();
        RunnerPrices runnerPrices = new RunnerPrices(232, List.of(new Price(2.3d, 22, Side.BACK)), 5d, 2.5d);
        MarketPrices marketPrices = new MarketPrices(1, market, List.of(runnerPrices));
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);
        BetAction betAction = CoreTestFactory.newBetAction("1", market);
        betAction.setMarketPrices(marketPrices);
        betActionDao.saveOrUpdate(betAction);

        List<BetAction> actions = betActionDao.getBetActions(market.getId(), OptionalLong.empty(), empty());
        assertThat(actions.size(), is(1));
    }

    @Test
    public void testSharedPrices() {
        Market market = newMarket();
        RunnerPrices runnerPrices = new RunnerPrices(232, List.of(new Price(2.3d, 22, Side.BACK)), 5d, 2.5d);
        MarketPrices marketPrices = new MarketPrices(1, market, Arrays.asList(runnerPrices));
        marketPrices.setTime(DateUtils.addMonths(new Date(), -1));
        marketDao.saveOrUpdate(market);
        marketPricesDao.saveOrUpdate(marketPrices);

        BetAction betAction1 = CoreTestFactory.newBetAction("1", market);
        betAction1.setMarketPrices(marketPrices);
        betActionDao.saveOrUpdate(betAction1);

        BetAction betAction2 = CoreTestFactory.newBetAction("2", market);
        betAction2.setMarketPrices(marketPrices);
        betActionDao.saveOrUpdate(betAction2);

        betActionDao.fetchMarketPrices(betAction1);
        betActionDao.fetchMarketPrices(betAction2);

        assertThat(betAction1.getMarketPrices().getId(), is(betAction2.getMarketPrices().getId()));
    }

}