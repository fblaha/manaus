package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import org.junit.Test;

import java.util.OptionalDouble;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NameAwareTest {

    @Test
    public void testName() {
        var proposer = new VeryProfitableProposer();
        assertThat(proposer.getName(), is("veryProfitableProposer"));
    }

    private static class VeryProfitableProposer implements PriceProposer {

        @Override
        public OptionalDouble getProposedPrice(BetContext context) {
            return OptionalDouble.of(5);
        }
    }

}