package cz.fb.manaus.reactor.profit;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Optional.empty;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProfitServiceTest extends AbstractProfitTest {
    @Autowired
    private ProfitService profitService;
    @Autowired
    private ExchangeProvider provider;


    @Test
    public void testSingleSelection() {
        var lay = new SettledBet(CoreTestFactory.DRAW, "The Draw", 5d, marketDate, new Price(2d, 4d, Side.LAY));
        var back = new SettledBet(CoreTestFactory.DRAW, "The Draw", -4.5d, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        setBetAction(lay, back);
        checkRecords(0.47d, null, lay, back);
    }

    @Test
    public void testMultiSelection() {
        var layDraw = new SettledBet(CoreTestFactory.DRAW, "The Draw", 5d, marketDate, new Price(2d, 4d, Side.LAY));
        var backDraw = new SettledBet(CoreTestFactory.DRAW, "The Draw", -4.9d, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        var layHome = new SettledBet(CoreTestFactory.HOME, "Home", 5d, marketDate, new Price(2d, 4d, Side.LAY));
        var backHome = new SettledBet(CoreTestFactory.HOME, "Home", -4.1d, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        setBetAction(layDraw, backDraw, layHome, backHome);
        checkRecords(0.935d, of("selectionRegexp_draw", 0.067), layHome, backHome, layDraw, backDraw);
    }


    @Test
    public void testRealBackWin() {
//22263	2012-04-26 15:37:47.0	2012-04-26 06:11:23.0	6.4	    2.14	1	-7.3	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        var lay = new SettledBet(CoreTestFactory.DRAW, "Over 2.5 Goals", -7.3, marketDate, new Price(2.14d, 6.4d, Side.LAY));
//22264	2012-04-26 15:53:26.0	2012-04-26 15:53:07.0	5.44	2.34	0	7.29	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        var back = new SettledBet(CoreTestFactory.DRAW, "Over 2.5 Goals", 7.29d, marketDate, new Price(2.34d, 5.44d, Side.BACK));
        setBetAction(lay, back);
        checkRecords(-0.01d, null, lay, back);
    }

    @Test
    public void testRealLayWin() {
        var kamazLay = createKamazLay();
        var kamazBack = createKamazBack();
        setBetAction(kamazBack, kamazLay);
        checkRecords(0.98d, null, kamazLay, kamazBack);
    }

    private SettledBet createKamazBack() {
        //22256	2012-04-26 12:40:33.0	2012-04-26 12:14:07.0	4.22	2.98	0	-4.22	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return new SettledBet(CoreTestFactory.DRAW, "Kamaz", -4.22, marketDate, new Price(2.98d, 4.22d, Side.BACK));
    }

    private SettledBet createKamazLay() {
        //22255	2012-04-26 06:56:08.0	2012-04-26 02:00:51.0	5.27	2.92	1	5.27	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return new SettledBet(CoreTestFactory.DRAW, "Kamaz", 5.27, marketDate, new Price(2.92d, 5.27d, Side.LAY));
    }

    @Test
    public void testSimulation() {
        var kamazBack = createKamazBack();
        var kamazLay = createKamazLay();
        setBetAction(kamazBack, kamazLay);
        var bets = List.of(kamazBack, kamazLay);
        var simulationOnly = profitService.getProfitRecords(bets, empty(), true,
                provider.getChargeRate());
        var all = profitService.getProfitRecords(bets, empty(), false,
                provider.getChargeRate());
        assertTrue(simulationOnly.size() < all.size());
        assertTrue(simulationOnly.size() > 0);
    }

    private void checkRecords(double expectedAllProfit, Map<String, Double> otherProfits, SettledBet... bets) {
        var betList = List.of(bets);
        var result = profitService.getProfitRecords(betList, empty(),
                false, provider.getChargeRate());
        var all = result.stream().filter(ProfitRecord::isAllCategory).findAny().get();
        assertEquals(expectedAllProfit, all.getProfit(), 0.01d);
        var backCount = (int) betList.stream().filter(bet -> bet.getPrice().getSide() == Side.BACK).count();
        var layCount = (int) betList.stream().filter(bet -> bet.getPrice().getSide() == Side.LAY).count();
        assertThat(all.getBackCount(), is(backCount));
        assertThat(all.getLayCount(), is(layCount));
        assertThat(all.getTotalCount(), is(layCount + backCount));
        if (otherProfits != null) {
            var byCategory = byCategory(result);
            for (var entry : otherProfits.entrySet()) {
                var record = byCategory.get(entry.getKey());
                assertEquals(entry.getValue(), record.getProfit(), 0.0001d);
            }
        }
    }

    @Test
    public void testGetProfitRecords() {
        var bet1 = new SettledBet(CoreTestFactory.HOME, "The Draw", 5d,
                addDays(marketDate, 1), new Price(2d, 4d, Side.LAY));
        bet1.setPlaced(addDays(marketDate, -1));
        var bet2 = new SettledBet(CoreTestFactory.HOME, "The Draw", -2d,
                addDays(marketDate, 1), new Price(2d, 5d, Side.BACK));
        bet2.setPlaced(addHours(marketDate, -1));
        setBetAction(bet1, bet2);
        var records = profitService.getProfitRecords(List.of(bet1, bet2), empty(), true,
                provider.getChargeRate());

        var byCategory = byCategory(records);


        assertThat(byCategory.get("market_country_br").getLayCount(), is(1));
        assertEquals(2.8d, byCategory.get("market_country_br").getProfit(), 0.01);

        assertThat(byCategory.get("placedBefore_hour_1-2").getBackCount(), is(1));
        assertEquals(-2d, byCategory.get("placedBefore_hour_1-2").getProfit(), 0.01);

        assertThat(byCategory.get("placedBefore_day_1-2").getLayCount(), is(1));
        assertEquals(4.8d, byCategory.get("placedBefore_day_1-2").getProfit(), 0.01);

        assertTrue(profitService.getProfitRecords(List.of(bet1, bet2), Optional.of("market_country_ua"), true,
                provider.getChargeRate()).isEmpty());
        assertFalse(profitService.getProfitRecords(List.of(bet1, bet2), Optional.of("market_country_br"), true,
                provider.getChargeRate()).isEmpty());

    }

    private Map<String, ProfitRecord> byCategory(List<ProfitRecord> records) {
        return records.stream().collect(toMap(ProfitRecord::getCategory, identity()));
    }

    @Test
    public void testMergeCategory() {
        var r1 = new ProfitRecord("test", 100d, 1, 1, 2d, 0.06);
        r1.setCoverDiff(0.2);
        r1.setCoverCount(1);
        var r2 = new ProfitRecord("test", 100d, 1, 1, 2d, 0.06);
        var record = profitService.mergeCategory("test", List.of(r1, r2));
        assertEquals(record.getCoverDiff(), r1.getCoverDiff(), 0.00001d);
    }
}
