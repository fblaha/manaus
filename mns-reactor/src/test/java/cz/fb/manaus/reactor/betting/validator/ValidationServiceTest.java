package cz.fb.manaus.reactor.betting.validator;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.betting.BetContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationServiceTest extends AbstractLocalTestCase {
    private static final String NOVGOROD = "novgorod";
    @Autowired
    private ValidationService service;

    @Test
    public void testReduceAcceptReject() throws Exception {
        assertThat(service.reduce(asList(ValidationResult.ACCEPT, ValidationResult.REJECT, ValidationResult.ACCEPT)), is(ValidationResult.REJECT));
        assertThat(service.reduce(asList(ValidationResult.ACCEPT, ValidationResult.ACCEPT)), is(ValidationResult.ACCEPT));
    }

    @Test
    public void testDowngradeAccepting() throws Exception {
        assertThat(new TestValidator(ValidationResult.REJECT).isDowngradeAccepting(), is(true));
    }

    @Test
    public void testDowngradePrice() throws Exception {
        checkDownGrade(2d, ValidationResult.ACCEPT);
    }

    @Test
    public void testUpgradePrice() throws Exception {
        checkDownGrade(2.4d, null);
    }

    private void checkDownGrade(double newPrice, ValidationResult expected) {
        Bet oldBet = mock(Bet.class);
        when(oldBet.getRequestedPrice()).thenReturn(new Price(2.2d, 2d, Side.LAY));
        TestValidator rejecting = new TestValidator(ValidationResult.REJECT);
        Optional<ValidationResult> result = service.handleDowngrade(
                Optional.of(new Price(newPrice, 2d, Side.LAY)),
                Optional.of(oldBet), rejecting);
        assertThat(result.orElse(null), is(expected));
        assertThat(rejecting.validate(null), is(ValidationResult.REJECT));
    }


    private static class TestValidator implements Validator {

        private final ValidationResult result;

        private TestValidator(ValidationResult result) {
            this.result = result;
        }

        @Override
        public ValidationResult validate(BetContext context) {
            return result;
        }
    }
}
