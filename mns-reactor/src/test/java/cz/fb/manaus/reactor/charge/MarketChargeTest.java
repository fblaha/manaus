package cz.fb.manaus.reactor.charge;

import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarketChargeTest extends AbstractLocalTestCase {

    public static final int SEL1 = 11;
    public static final int SEL2 = 22;
    @Autowired
    private ExchangeProvider provider;
    private Date current = new Date();
    /**
     * Comm Charged 5% On Net winnings of EUR0.2	 	 -	-	-	(0.01)	 	1,298.08
     * 16:36	 Qualifying Matches Bozoljac v Hernych / Match Odds / Ilia Bozoljac     Back	 2.98	2.00	Lost	(2.00)	 	1,298.09
     * 16:13	 Qualifying Matches Bozoljac v Hernych / Match Odds / Ilia Bozoljac     Lay	 	 2.60	2.00	Won	 	2.00    	1,300.09
     * 17:11	 Qualifying Matches Bozoljac v Hernych / Match Odds / Jan Hernych       Back 	 1.63	2.00	Won	 	1.26	    1,298.09
     * 16:36	 Qualifying Matches Bozoljac v Hernych / Match Odds / Jan Hernych       Lay	 	 1.53	2.00	Lost	(1.06)	 	1,296.83
     */
    private SettledBet back1 = mockAction(
            new SettledBet(SEL1, "Ilia Bozoljac", -2d, current, new Price(2.98d, 2d, Side.BACK)), "1");
    private SettledBet lay1 = mockAction(
            new SettledBet(SEL1, "Ilia Bozoljac", 2d, current, new Price(2.6d, 2d, Side.LAY)), "2");
    private SettledBet back2 = mockAction(
            new SettledBet(SEL2, "Jan Hernych", 1.26d, current, new Price(1.63d, 2d, Side.BACK)), "3");
    private SettledBet lay2 = mockAction(
            new SettledBet(SEL2, "Jan Hernych", -1.06d, current, new Price(1.53d, 2d, Side.LAY)), "4");

    private SettledBet mockAction(SettledBet bet, String betId) {
        BetAction mock = mock(BetAction.class);
        when(mock.getBetId()).thenReturn(betId);
        bet.setBetAction(mock);
        return bet;
    }


    @Test
    public void testChargeLowProfit() throws Exception {
        MarketCharge charge = MarketCharge.fromBets(provider.getChargeRate(), List.of(lay1, lay2, back1, back2));
        checkCharge(charge, 0.013d, 0.2d, of("2", 0.01d, "3", 0d));
    }

    @Test
    public void testChargeHighProfit() throws Exception {
        MarketCharge charge = MarketCharge.fromBets(provider.getChargeRate(), List.of(lay1, lay2, back2));
        checkCharge(charge, 0.143d, 2.2d, of("2", 0.09d, "3", 0.05d));
    }

    @Test
    public void testChargeLoss() throws Exception {
        MarketCharge charge = MarketCharge.fromBets(provider.getChargeRate(), List.of(lay1, lay2, back1));
        checkCharge(charge, 0d, -1.06d, of("2", 0d, "3", 0d));
    }

    @Test
    public void testOneLossOneProfit() throws Exception {
        MarketCharge charge = MarketCharge.fromBets(provider.getChargeRate(), List.of(lay1, lay2));
        checkCharge(charge, 0.061d, 0.94d, of("2", 0.06d, "4", 0d));
    }

    private void checkCharge(MarketCharge charge, double totalCharge, double totalProfit, Map<String, Double> expectedContibutions) {
        assertThat(charge.getTotalCharge(), is(totalCharge));
        assertThat(charge.getTotalProfit(), is(totalProfit));
        for (Map.Entry<String, Double> entry : expectedContibutions.entrySet()) {
            assertEquals(entry.getValue().doubleValue(), charge.getChargeContribution(entry.getKey()), 0.01);
        }
    }

}
