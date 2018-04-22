package cz.fb.manaus.reactor.betting.validator.common.update;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class _AbstractTooCloseUpdateValidatorTest extends AbstractLocalTestCase {

    @Autowired
    private TestValidator validator;
    @Autowired
    private RoundingService roundingService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private ReactorTestFactory factory;
    @Autowired
    private ExchangeProvider provider;

    private MarketPrices prices;
    private RunnerPrices runnerPrices;

    @Before
    public void setUp() throws Exception {
        prices = factory.createMarket(0.1, List.of(0.4, 0.3, 0.3));
        runnerPrices = prices.getRunnerPrices(CoreTestFactory.HOME);
    }

    @Test
    public void testAcceptBack() throws Exception {
        Price oldPrice = new Price(2.5d, 5d, Side.BACK);
        Bet oldBet = ReactorTestFactory.newBet(oldPrice);

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(oldPrice)), is(ValidationResult.REJECT));

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 1).get())), is(ValidationResult.REJECT));

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 2).get())), is(ValidationResult.REJECT));

        assertThat(validator.validate(factory.newBetContext(Side.BACK, prices, runnerPrices, of(oldBet))
                .withNewPrice(roundingService.decrement(oldPrice, 3).get())), is(ValidationResult.ACCEPT));
    }

    @Test
    public void testAcceptLay() throws Exception {
        Price newOne = mock(Price.class);
        Price oldOne = mock(Price.class);
        when(newOne.getSide()).thenReturn(Side.LAY);
        when(oldOne.getSide()).thenReturn(Side.LAY);
        when(newOne.getPrice()).thenReturn(3.15d);
        when(oldOne.getPrice()).thenReturn(3.1d);
        Bet oldBet = ReactorTestFactory.newBet(oldOne);

        BetContext context = factory.newBetContext(Side.LAY, prices, runnerPrices, of(oldBet)).withNewPrice(newOne);
        assertThat(validator.validate(context), is(ValidationResult.REJECT));
        when(newOne.getPrice()).thenReturn(3.2d);
        assertThat(validator.validate(context), is(ValidationResult.REJECT));
        when(newOne.getPrice()).thenReturn(3.05d);
        assertThat(validator.validate(context), is(ValidationResult.REJECT));
        when(newOne.getPrice()).thenReturn(3.25d);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));
    }

    @Test
    public void testMinimalPrice() throws Exception {
        Price newOne = mock(Price.class);
        Price oldOne = mock(Price.class);
        when(newOne.getSide()).thenReturn(Side.LAY);
        when(oldOne.getSide()).thenReturn(Side.LAY);
        when(oldOne.getPrice()).thenReturn(provider.getMinPrice());
        when(newOne.getPrice()).thenReturn(1.04d);
        Bet oldBet = ReactorTestFactory.newBet(oldOne);

        BetContext context = factory.newBetContext(Side.LAY, prices, runnerPrices, of(oldBet)).withNewPrice(newOne);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));
    }

    @Component
    final private static class TestValidator extends AbstractTooCloseUpdateValidator {
        public TestValidator() {
            super(Set.of(-2, -1, 1, 2));
        }

    }

}