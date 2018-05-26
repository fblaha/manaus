package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class _AbstractFairnessProposerTest extends AbstractLocalTestCase {

    @Autowired
    private TestLayProposer layProposer;
    @Autowired
    private TestBackProposer backProposer;
    @Autowired
    private ReactorTestFactory factory;

    @Test
    public void testLayByLayPropose() {
        var ctx = factory.createContext(Side.LAY, 2.5, 3.2);
        var proposedPrice = layProposer.getProposedPrice(ctx);
        assertThat(Price.round(proposedPrice.getAsDouble()), is(2.96d));
    }

    @Test
    public void testBackByLayPropose() {
        var ctx = factory.createContext(Side.BACK, 2.5, 3.5);
        var proposedPrice = layProposer.getProposedPrice(ctx);
        assertThat(Price.round(proposedPrice.getAsDouble()), is(3.041d));
    }

    @Test
    public void testBackByBackPropose() {
        var ctx = factory.createContext(Side.BACK, 2.8, 3.5);
        var proposedPrice = backProposer.getProposedPrice(ctx);
        assertThat(Price.round(proposedPrice.getAsDouble()), is(3.041d));
    }

    @Test
    public void testLayByBackPropose() {
        var ctx = factory.createContext(Side.LAY, 2.2, 3.7);
        var proposedPrice = backProposer.getProposedPrice(ctx);
        assertThat(Price.round(proposedPrice.getAsDouble()), is(2.96d));
    }

    @Component
    private static class TestLayProposer extends AbstractFairnessProposer {
        public TestLayProposer() {
            super(Side.LAY, stage -> 0.02d);
        }
    }

    @Component
    private static class TestBackProposer extends AbstractFairnessProposer {
        public TestBackProposer() {
            super(Side.BACK, stage -> 0.02d);
        }
    }


}