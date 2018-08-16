package cz.fb.manaus.reactor.betting.validator.common.update;

import cz.fb.manaus.core.dao.AbstractDaoTest;
import cz.fb.manaus.core.model.BetActionFactory;
import cz.fb.manaus.core.model.BetActionType;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.MarketPricesFactory;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import cz.fb.manaus.reactor.ReactorTestFactory;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static cz.fb.manaus.spring.ManausProfiles.DB;
import static cz.fb.manaus.spring.ManausProfiles.TEST;
import static org.hamcrest.CoreMatchers.is;

@ActiveProfiles(value = {"matchbook", TEST, DB}, inheritProfiles = false)
public class _AbstractDelayUpdateValidatorTest extends AbstractDaoTest {
    @Autowired
    private TestValidator validator;
    @Autowired
    private ReactorTestFactory factory;


    private void checkValidation(BetActionType actionType, int beforeMinutes, Side lay, ValidationResult validationResult) {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var place = BetActionFactory.create(actionType, DateUtils.addMinutes(new Date(), -beforeMinutes), new Price(2d, 30d, lay), market, CoreTestFactory.HOME);
        place.setBetId(ReactorTestFactory.BET_ID);
        betActionDao.saveOrUpdate(place);
        var runnerPrices = new RunnerPrices();
        runnerPrices.setSelectionId(CoreTestFactory.HOME);

        var marketPrices = MarketPricesFactory.create(1, market, List.of(runnerPrices), new Date());
        var result = validator.validate(factory.newUpdateBetContext(marketPrices, runnerPrices, lay));
        Assert.assertThat(result, is(validationResult));
    }

    @Test(expected = NoSuchElementException.class)
    public void testNoBetAction() {
        var market = newMarket();
        marketDao.saveOrUpdate(market);
        var marketPrices = MarketPricesFactory.create(1, market, List.of(), new Date());
        var runnerPrices = new RunnerPrices();
        runnerPrices.setSelectionId(CoreTestFactory.DRAW);
        var result = validator.validate(factory.newUpdateBetContext(marketPrices, runnerPrices, Side.LAY));
        Assert.assertThat(result, is(ValidationResult.REJECT));
    }

    @Test
    public void testClosePlace() {
        checkValidation(BetActionType.PLACE, 29, Side.LAY, ValidationResult.REJECT);
    }

    @Test
    public void testFarPlace() {
        checkValidation(BetActionType.PLACE, 31, Side.LAY, ValidationResult.ACCEPT);
    }


    @Test
    public void testCloseUpdate() {
        checkValidation(BetActionType.UPDATE, 29, Side.LAY, ValidationResult.REJECT);
    }

    @Test
    public void testFarPlaceBack() {
        checkValidation(BetActionType.PLACE, 31, Side.BACK, ValidationResult.ACCEPT);
    }


    @Test
    public void testCloseUpdateBack() {
        checkValidation(BetActionType.UPDATE, 15, Side.BACK, ValidationResult.REJECT);
    }

    @Test
    public void testFarUpdate() {
        checkValidation(BetActionType.UPDATE, 150, Side.BACK, ValidationResult.ACCEPT);
    }


    private Market newMarket() {
        return CoreTestFactory.newMarket("33", DateUtils.addHours(new Date(), 2), CoreTestFactory.MATCH_ODDS);
    }

    @Component
    @Profile(DB)
    private static class TestValidator extends AbstractDelayUpdateValidator {

        public TestValidator() {
            super(Duration.ofMinutes(30));
        }

    }

}
