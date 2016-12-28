package cz.fb.manaus.reactor.betting.proposer;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.betting.BetContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalDouble;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PriceProposalServiceTest extends AbstractLocalTestCase {

    public static final List<PriceProposer> PROPOSERS = Arrays.asList(
            new TestProposer1(),
            new TestProposer2(),
            new FooProposer());
    public static final double MIN_PRICE = 1.5d;
    public static final double MAX_PRICE = 2d;
    @Autowired
    private PriceProposalService service;

    @Test
    public void testBackPrice() throws Exception {
        checkProposal(MAX_PRICE, Side.BACK, "testProposer2,fooProposer", PROPOSERS);
    }

    @Test
    public void testLayPrice() throws Exception {
        checkProposal(MIN_PRICE, Side.LAY, "testProposer1", PROPOSERS);
    }

    @Test(expected = IllegalStateException.class)
    public void testMandatoryPrice() throws Exception {
        PriceProposer proposer = ctx -> OptionalDouble.empty();
        service.reducePrices(mock(BetContext.class), Collections.singletonList(proposer), Side.LAY);
    }

    private void checkProposal(double expectedPrice, Side side, String proposer, List<PriceProposer> proposers) {
        BetContext context = mock(BetContext.class);
        when(context.getProperties()).thenReturn(new HashMap<>());
        double price = service.reducePrices(context, proposers, side).getPrice();
        assertThat(price, is(expectedPrice));
    }

    private static class TestProposer1 implements PriceProposer {

        @Override
        public OptionalDouble getProposedPrice(BetContext context) {
            return OptionalDouble.of(MIN_PRICE);
        }
    }

    private static class TestProposer2 implements PriceProposer {

        @Override
        public OptionalDouble getProposedPrice(BetContext context) {
            return OptionalDouble.of(MAX_PRICE);
        }
    }

    private static class FooProposer extends TestProposer2 {
    }


}