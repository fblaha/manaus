package cz.fb.manaus.reactor.profit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.categorizer.namespace.ExactPriceCategorizer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Iterables.find;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
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
    public void testSingleSelection() throws Exception {
        SettledBet lay = new SettledBet(CoreTestFactory.DRAW, "The Draw", 5d, marketDate, marketDate, new Price(2d, 4d, Side.LAY));
        SettledBet back = new SettledBet(CoreTestFactory.DRAW, "The Draw", -4.5d, marketDate, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        setBetAction(lay, back);
        checkRecords(0.47d, null, lay, back);
    }

    @Test
    public void testMultiSelection() throws Exception {
        SettledBet layDraw = new SettledBet(CoreTestFactory.DRAW, "The Draw", 5d, marketDate, marketDate, new Price(2d, 4d, Side.LAY));
        SettledBet backDraw = new SettledBet(CoreTestFactory.DRAW, "The Draw", -4.9d, marketDate, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        SettledBet layHome = new SettledBet(CoreTestFactory.HOME, "Home", 5d, marketDate, marketDate, new Price(2d, 4d, Side.LAY));
        SettledBet backHome = new SettledBet(CoreTestFactory.HOME, "Home", -4.1d, marketDate, marketDate, new Price(2.2d, 3.5d, Side.BACK));
        setBetAction(layDraw, backDraw, layHome, backHome);
        checkRecords(0.935d, of("selectionRegexp_draw", 0.067), layHome, backHome, layDraw, backDraw);
    }


    @Test
    public void testRealBackWin() throws Exception {
//22263	2012-04-26 15:37:47.0	2012-04-26 06:11:23.0	6.4	    2.14	1	-7.3	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        SettledBet lay = new SettledBet(CoreTestFactory.DRAW, "Over 2.5 Goals", -7.3, marketDate, marketDate, new Price(2.14d, 6.4d, Side.LAY));
//22264	2012-04-26 15:53:26.0	2012-04-26 15:53:07.0	5.44	2.34	0	7.29	47973	Over 2.5 Goals	105524600	105524600	RUS	2012-04-26 16:00:00.0	1524.3	Over/Under 2.5 goals	26837433
        SettledBet back = new SettledBet(CoreTestFactory.DRAW, "Over 2.5 Goals", 7.29d, marketDate, marketDate, new Price(2.34d, 5.44d, Side.BACK));
        setBetAction(lay, back);
        checkRecords(-0.01d, null, lay, back);
    }

    @Test
    public void testRealLayWin() throws Exception {
        SettledBet kamazLay = createKamazLay();
        SettledBet kamazBack = createKamazBack();
        setBetAction(kamazBack, kamazLay);
        checkRecords(0.98d, null, kamazLay, kamazBack);
    }

    private SettledBet createKamazBack() {
        //22256	2012-04-26 12:40:33.0	2012-04-26 12:14:07.0	4.22	2.98	0	-4.22	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return new SettledBet(CoreTestFactory.DRAW, "Kamaz", -4.22, marketDate, marketDate, new Price(2.98d, 4.22d, Side.BACK));
    }

    private SettledBet createKamazLay() {
        //22255	2012-04-26 06:56:08.0	2012-04-26 02:00:51.0	5.27	2.92	1	5.27	2460921	Kamaz	105486372	105486372	RUS	2012-04-26 16:00:00.0	4314.43	Match Odds	26836220
        return new SettledBet(CoreTestFactory.DRAW, "Kamaz", 5.27, marketDate, marketDate, new Price(2.92d, 5.27d, Side.LAY));
    }

    @Test
    public void testSimulation() throws Exception {
        SettledBet kamazBack = createKamazBack();
        SettledBet kamazLay = createKamazLay();
        setBetAction(kamazBack, kamazLay);
        List<SettledBet> bets = Arrays.asList(kamazBack, kamazLay);
        List<ProfitRecord> simulationOnly = profitService.getProfitRecords(bets, empty(), true, empty(),
                provider.getChargeRate());
        List<ProfitRecord> all = profitService.getProfitRecords(bets, empty(), false, empty(),
                provider.getChargeRate());
        assertTrue(simulationOnly.size() < all.size());
        assertTrue(simulationOnly.size() > 0);
    }

    @Test
    public void testNamespace() throws Exception {
        SettledBet kamazBack = createKamazBack();
        SettledBet kamazLay = createKamazLay();
        setBetAction(kamazBack, kamazLay);
        List<ProfitRecord> records = profitService.getProfitRecords(Arrays.asList(kamazBack, kamazLay), empty(), false,
                Optional.of(ExactPriceCategorizer.NAMESPACE),
                provider.getChargeRate());
        Set<String> categories = records.stream().map(ProfitRecord::getCategory).collect(Collectors.toSet());
        assertThat(records.size(), is(3));
        assertThat(categories, hasItems(MarketCategories.ALL,
                ExactPriceCategorizer.NAMESPACE + "_2.98",
                ExactPriceCategorizer.NAMESPACE + "_2.92"));
    }

    private void checkRecords(double expectedAllProfit, Map<String, Double> otherProfits, SettledBet... bets) {
        List<SettledBet> betList = Arrays.asList(bets);
        List<ProfitRecord> result = profitService.getProfitRecords(betList, empty(),
                false, empty(), provider.getChargeRate());
        ProfitRecord all = find(result, ProfitRecord::isAllCategory);
        assertEquals(expectedAllProfit, all.getProfit(), 0.01d);
        int backCount = (int) betList.stream().filter(bet -> bet.getPrice().getSide() == Side.BACK).count();
        int layCount = (int) betList.stream().filter(bet -> bet.getPrice().getSide() == Side.LAY).count();
        assertThat(all.getBackCount(), is(backCount));
        assertThat(all.getLayCount(), is(layCount));
        assertThat(all.getTotalCount(), is(layCount + backCount));
        if (otherProfits != null) {
            ImmutableMap<String, ProfitRecord> byCategory = byCategory(result);
            for (Map.Entry<String, Double> entry : otherProfits.entrySet()) {
                ProfitRecord record = byCategory.get(entry.getKey());
                assertEquals(entry.getValue(), record.getProfit(), 0.0001d);
            }
        }
    }

    @Test
    public void testGetProfitRecords() throws Exception {
        SettledBet bet1 = new SettledBet(CoreTestFactory.HOME, "The Draw", 5d, addDays(marketDate, -1),
                addDays(marketDate, 1), new Price(2d, 4d, Side.LAY));
        SettledBet bet2 = new SettledBet(CoreTestFactory.HOME, "The Draw", -2d, addHours(marketDate, -1),
                addDays(marketDate, 1), new Price(2d, 5d, Side.BACK));
        setBetAction(bet1, bet2);
        List<ProfitRecord> records = profitService.getProfitRecords(asList(bet1, bet2), empty(), true, empty(),
                provider.getChargeRate());

        Map<String, ProfitRecord> byCategory = byCategory(records);
        records.forEach(System.out::println);


        assertThat(byCategory.get("market_country_br").getLayCount(), is(1));
        assertEquals(2.8d, byCategory.get("market_country_br").getProfit(), 0.01);

        assertThat(byCategory.get("placedBefore_hour_1-2").getBackCount(), is(1));
        assertEquals(-2d, byCategory.get("placedBefore_hour_1-2").getProfit(), 0.01);

        assertThat(byCategory.get("placedBefore_day_1-2").getLayCount(), is(1));
        assertEquals(4.8d, byCategory.get("placedBefore_day_1-2").getProfit(), 0.01);

        assertTrue(profitService.getProfitRecords(asList(bet1, bet2), Optional.of("market_country_ua"), true, empty(),
                provider.getChargeRate()).isEmpty());
        assertFalse(profitService.getProfitRecords(asList(bet1, bet2), Optional.of("market_country_br"), true, empty(),
                provider.getChargeRate()).isEmpty());

    }

    private ImmutableMap<String, ProfitRecord> byCategory(List<ProfitRecord> records) {
        return Maps.uniqueIndex(records, ProfitRecord::getCategory);
    }

    @Test
    public void testMerge() throws Exception {
        ProfitRecord r1 = new ProfitRecord("test", 100d, 1, 1, 2d, 0.06);
        r1.setCoverDiff(0.2);
        r1.setCoverCount(1);
        ProfitRecord r2 = new ProfitRecord("test", 100d, 1, 1, 2d, 0.06);
        ProfitRecord record = profitService.mergeProfitRecords(Arrays.asList(r1, r2));
        assertEquals(record.getCoverDiff(), r1.getCoverDiff(), 0.00001d);
    }
}
