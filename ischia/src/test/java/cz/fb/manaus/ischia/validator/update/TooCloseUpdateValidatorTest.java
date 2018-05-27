package cz.fb.manaus.ischia.validator.update;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@ActiveProfiles("ischia")
public class TooCloseUpdateValidatorTest extends AbstractLocalTestCase {

    @Autowired
    private TooCloseUpdateValidator validator;
    @Autowired
    private ReactorTestFactory factory;

    @Test
    public void testValidate() throws Exception {
        BetContext context = factory.createContext(Side.BACK, 2.5, 3.5);
        assertThat(validator.validate(context), is(ValidationResult.ACCEPT));
    }

}