package cz.fb.manaus.reactor.betting.validator.common;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.OptionalDouble;

import static cz.fb.manaus.spring.ManausProfiles.TEST_PROFILE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles(value = {"matchbook", TEST_PROFILE}, inheritProfiles = false)
public class _AbstractLastMatchedValidatorTest extends AbstractLocalTestCase {

    @Autowired
    private TestValidator validator;
    @Autowired
    private ReactorTestFactory factory;

    @Test
    public void testAcceptLay() {
        checkValidator(Side.LAY, ValidationResult.ACCEPT, ValidationResult.REJECT);
    }

    @Test
    public void testAcceptBack() {
        checkValidator(Side.BACK, ValidationResult.REJECT, ValidationResult.ACCEPT);
    }

    private void checkValidator(Side side, ValidationResult lowerResult, ValidationResult higherResult) {
        var runnerPrices = mock(RunnerPrices.class);
        when(runnerPrices.getLastMatchedPrice()).thenReturn(2.1d);
        when(runnerPrices.getSelectionId()).thenReturn(CoreTestFactory.HOME);
        var marketPrices = mock(MarketPrices.class);
        when(marketPrices.getRunnerPrices(anyLong())).thenReturn(runnerPrices);
        when(marketPrices.getReciprocal(Side.BACK)).thenReturn(OptionalDouble.of(0.9d));
        assertThat(validator.validate(factory.newBetContext(side, marketPrices, runnerPrices, Optional.empty())
                .withNewPrice(new Price(2d, 2d, side))), is(lowerResult));
        assertThat(validator.validate(factory.newBetContext(side, marketPrices, runnerPrices, Optional.empty())
                .withNewPrice(new Price(2.2d, 2d, side))), is(higherResult));
    }

    @Component
    private static class TestValidator extends AbstractLastMatchedValidator {

        public TestValidator() {
            super(true);
        }
    }

}

