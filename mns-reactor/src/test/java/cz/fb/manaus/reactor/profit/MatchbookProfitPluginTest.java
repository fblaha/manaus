package cz.fb.manaus.reactor.profit;

import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static cz.fb.manaus.spring.CoreLocalConfiguration.TEST_PROFILE;
import static org.junit.Assert.assertEquals;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class MatchbookProfitPluginTest extends AbstractLocalTestCase {

    @Autowired
    private MatchbookProfitPlugin plugin;
    @Autowired
    private ExchangeProvider provider;


    @Test
    public void testChargeBackWin() throws Exception {
        assertEquals(0.207d, plugin.getCharge(provider.getChargeRate(), 27.6, 10), 0.001);
        assertEquals(0.0381d, plugin.getCharge(provider.getChargeRate(), 5.08, 2), 0.001);
    }

    @Test
    public void testChargeBackLoss() throws Exception {
        assertEquals(0.015d, plugin.getCharge(provider.getChargeRate(), -2, 2), 0.0001);
        assertEquals(0.075, plugin.getCharge(provider.getChargeRate(), -10, 10), 0.0001);
    }

    @Test
    public void testChargeLayWin() throws Exception {
        assertEquals(0.015d, plugin.getCharge(provider.getChargeRate(), 2, 2), 0.001);
        assertEquals(0.075d, plugin.getCharge(provider.getChargeRate(), 10, 10), 0.001);
    }

    @Test
    public void testChargeLayLoss() throws Exception {
        assertEquals(0.015d, plugin.getCharge(provider.getChargeRate(), -4.68, 2), 0.0001);
        assertEquals(0.06, plugin.getCharge(provider.getChargeRate(), -8, 10), 0.0001);
    }

}