package cz.fb.manaus.reactor.rounding;

import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static cz.fb.manaus.spring.CoreLocalConfiguration.TEST_PROFILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class MatchbookRoundingPluginTest extends AbstractLocalTestCase {

    @Autowired
    private MatchbookRoundingPlugin plugin;
    @Autowired
    private ExchangeProvider provider;


    @Test
    public void testShift() throws Exception {
        assertEquals(2.02, plugin.shift(2, 1).getAsDouble(), 0.0001d);
    }

    @Test
    public void testInc1x() throws Exception {
        assertEquals(1.227, plugin.shift(1.222d, 1).getAsDouble(), 0.001d);
        assertEquals(1.128, plugin.shift(1.125d, 1).getAsDouble(), 0.001d);
        assertEquals(1.131, plugin.shift(1.128d, 1).getAsDouble(), 0.001d);
        assertEquals(1.13, plugin.shift(1.125d, 2).getAsDouble(), 0.001d);
        assertEquals(2.01d, plugin.shift(1.99d, 1).getAsDouble(), 0.001d);
    }

    @Test
    public void testDec2x() throws Exception {
        assertEquals(1.98d, plugin.shift(2d, -1).getAsDouble(), 0.0001d);
        assertEquals(2.764d, plugin.shift(2.8d, -1).getAsDouble(), 0.0001d);
    }

    @Test
    public void testDec3x() throws Exception {
        assertEquals(2.96d, plugin.shift(3d, -1).getAsDouble(), 0.0001d);
    }

    @Test
    public void testInc3x() throws Exception {
        assertEquals(3.55d, plugin.shift(3.5d, 1).getAsDouble(), 0.0001d);
    }

    @Test
    public void testInc4x() throws Exception {
        assertEquals(5.08d, plugin.shift(5d, 1).getAsDouble(), 0.0001d);
    }

    @Test
    public void testRoundBigTest() throws Exception {
        double previous = -1;
        for (double price = provider.getMinPrice(); price < 5; price += 0.001) {
            double current = plugin.round(price).getAsDouble();
            assertTrue(previous <= current);
            previous = current;
        }
    }

}