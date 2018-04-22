package cz.fb.manaus.reactor.betting.validator.common.update;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class _AbstractTooCloseUpdateEpsilonValidatorTest extends AbstractLocalTestCase {

    @Autowired
    private TestValidator validator;
    @Autowired
    private RoundingService roundingService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ReactorTestFactory factory;


    @Test
    public void testAcceptBack() throws Exception {
        Price oldPrice = new Price(2.5d, 5d, Side.BACK);
        Optional<Bet> oldBet = Optional.of(ReactorTestFactory.newBet(oldPrice));

        MarketPrices prices = factory.createMarket(0.1, List.of(0.4, 0.3, 0.3));
        RunnerPrices runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME);
        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(oldPrice)), is(ValidationResult.REJECT));

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(roundingService.decrement(oldPrice, 1).get())), is(ValidationResult.REJECT));

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, oldBet)
                .withNewPrice(roundingService.decrement(oldPrice, 3).get())), is(ValidationResult.ACCEPT));
    }

    @Test
    public void testAcceptLay() throws Exception {
        Price newOne = mock(Price.class);
        Price oldOne = mock(Price.class);
        when(newOne.getSide()).thenReturn(Side.LAY);
        when(oldOne.getSide()).thenReturn(Side.LAY);
        when(oldOne.getPrice()).thenReturn(3.6d);
        Optional<Bet> oldBet = Optional.of(ReactorTestFactory.newBet(oldOne));

        MarketPrices prices = factory.createMarket(0.1, List.of(0.4, 0.3, 0.3));
        RunnerPrices runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME);

        BetContext context = factory.newBetContext(Side.LAY, prices, runnerPrices, oldBet).withNewPrice(newOne);
        when(newOne.getPrice()).thenReturn(3.65d);
        assertThat(validator.validate(context), is(ValidationResult.REJECT));
        when(newOne.getPrice()).thenReturn(3.7d);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));
        when(newOne.getPrice()).thenReturn(3.75d);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));

        when(newOne.getPrice()).thenReturn(3.55d);
        assertThat(validator.validate(context), is(ValidationResult.REJECT));
        when(newOne.getPrice()).thenReturn(3.5d);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));
    }

    @Component
    final private static class TestValidator extends AbstractTooCloseUpdateEpsilonValidator {
        public TestValidator() {
            super(0.02);
        }

    }

}