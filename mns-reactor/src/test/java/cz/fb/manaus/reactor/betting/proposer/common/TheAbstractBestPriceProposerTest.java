package cz.fb.manaus.reactor.betting.proposer.common;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TheAbstractBestPriceProposerTest extends AbstractLocalTestCase {

    @Autowired
    private LayProposer layProposer;
    @Autowired
    private BackProposer backProposer;
    @Autowired
    private ReactorTestFactory factory;

    @Test
    public void testLayPropose() {
        var context = mock(BetContext.class);
        when(context.getSide()).thenReturn(Side.LAY);
        when(context.getRunnerPrices()).thenReturn(factory.newRP(CoreTestFactory.HOME, 2d, 4.5d));
        assertThat(layProposer.validate(context), is(ValidationResult.ACCEPT));
        assertThat(layProposer.getProposedPrice(context).getAsDouble(), is(2.02d));
    }

    @Test
    public void testCheck() {
        var context = mock(BetContext.class);
        when(context.getSide()).thenReturn(Side.LAY, Side.BACK);
        when(context.getRunnerPrices()).thenReturn(CoreTestFactory.newBackRP(2d, CoreTestFactory.HOME, 2d));
        assertThat(layProposer.validate(context), is(ValidationResult.ACCEPT));
        assertThat(backProposer.validate(context), is(ValidationResult.REJECT));
    }

    @Test
    public void testBackPropose() {
        var context = mock(BetContext.class);
        when(context.getSide()).thenReturn(Side.BACK);
        when(context.getRunnerPrices()).thenReturn(factory.newRP(CoreTestFactory.HOME, 2.5d, 3.5d));
        assertThat(backProposer.validate(context), is(ValidationResult.ACCEPT));
        assertThat(backProposer.getProposedPrice(context).getAsDouble(), is(3.45d));
    }

    @Component
    private static class LayProposer extends AbstractBestPriceProposer {

        public LayProposer() {
            super();
        }
    }

    @Component
    private static class BackProposer extends AbstractBestPriceProposer {

        public BackProposer() {
            super();
        }
    }

}
