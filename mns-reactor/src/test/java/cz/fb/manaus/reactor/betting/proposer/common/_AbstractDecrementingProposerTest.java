package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.OptionalDouble;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class _AbstractDecrementingProposerTest extends AbstractLocalTestCase {

    @Autowired
    private TestProposer proposer;

    @Test
    public void testPropose() throws Exception {
        proposer.setOrigPrice(2d);
        assertThat(proposer.getProposedPrice(null).getAsDouble(), is(1.98d));
    }

    @Test(expected = NoSuchElementException.class)
    public void testLowPrice() throws Exception {
        proposer.setOrigPrice(1.02d);
        OptionalDouble proposedPrice = proposer.getProposedPrice(null);
        System.out.println("proposedPrice = " + proposedPrice.getAsDouble());
    }

    @Test
    public void testCheckVeto() throws Exception {
        proposer.setOrigPrice(1.02d);
        assertThat(proposer.validate(null), is(ValidationResult.REJECT));
    }

    @Test
    public void testCheckOk() throws Exception {
        proposer.setOrigPrice(1.2d);
        assertThat(proposer.validate(null), is(ValidationResult.ACCEPT));
    }

    @Component
    private static class TestProposer extends AbstractDecrementingProposer {

        private double origPrice;

        public TestProposer() {
            super(2);
        }

        public void setOrigPrice(double origPrice) {
            this.origPrice = origPrice;
        }

        @Override
        protected double getOriginalPrice(BetContext betContext) {
            return origPrice;
        }
    }

}