package cz.fb.manaus.reactor.betting.validator.common.update

import cz.fb.manaus.core.dao.AbstractDaoTest
import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.CoreTestFactory
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import cz.fb.manaus.spring.ManausProfiles.DB
import cz.fb.manaus.spring.ManausProfiles.TEST
import org.apache.commons.lang3.time.DateUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.util.*
import kotlin.test.assertEquals

@ActiveProfiles(value = *arrayOf("matchbook", TEST, DB), inheritProfiles = false)
class TheAbstractDelayUpdateValidatorTest : AbstractDaoTest() {
    @Autowired
    private lateinit var validator: TestValidator
    @Autowired
    private lateinit var reactorTestFactory: ReactorTestFactory


    private fun checkValidation(actionType: BetActionType, beforeMinutes: Int, lay: Side, validationResult: ValidationResult) {
        val market = newMarket()
        marketDao.saveOrUpdate(market)
        val place = ModelFactory.newAction(actionType, DateUtils.addMinutes(Date(), -beforeMinutes), Price(2.0, 30.0, lay), market, CoreTestFactory.HOME)
        place.betId = ReactorTestFactory.BET_ID
        betActionDao.saveOrUpdate(place)
        val runnerPrices = RunnerPrices()
        runnerPrices.selectionId = CoreTestFactory.HOME

        val marketPrices = ModelFactory.newPrices(1, market, listOf(runnerPrices), Date())
        val result = validator.validate(reactorTestFactory.newUpdateBetContext(marketPrices, runnerPrices, lay))
        assertEquals(validationResult, result)
    }

    @Test(expected = NoSuchElementException::class)
    fun `no bet action`() {
        val market = newMarket()
        marketDao.saveOrUpdate(market)
        val marketPrices = ModelFactory.newPrices(1, market, listOf(), Date())
        val runnerPrices = RunnerPrices()
        runnerPrices.selectionId = CoreTestFactory.DRAW
        val result = validator.validate(reactorTestFactory.newUpdateBetContext(marketPrices, runnerPrices, Side.LAY))
        assertEquals(ValidationResult.REJECT, result)
    }

    @Test
    fun `close place`() {
        checkValidation(BetActionType.PLACE, 29, Side.LAY, ValidationResult.REJECT)
    }

    @Test
    fun `far place`() {
        checkValidation(BetActionType.PLACE, 31, Side.LAY, ValidationResult.ACCEPT)
    }


    @Test
    fun `early update`() {
        checkValidation(BetActionType.UPDATE, 29, Side.LAY, ValidationResult.REJECT)
    }

    @Test
    fun `far place back`() {
        checkValidation(BetActionType.PLACE, 31, Side.BACK, ValidationResult.ACCEPT)
    }


    @Test
    fun `close update back`() {
        checkValidation(BetActionType.UPDATE, 15, Side.BACK, ValidationResult.REJECT)
    }

    @Test
    fun `far update`() {
        checkValidation(BetActionType.UPDATE, 150, Side.BACK, ValidationResult.ACCEPT)
    }

    private fun newMarket(): Market {
        return CoreTestFactory.newMarket("33", DateUtils.addHours(Date(), 2), CoreTestFactory.MATCH_ODDS)
    }

    @Component
    @Profile(DB)
    private class TestValidator : AbstractDelayUpdateValidator(Duration.ofMinutes(30))

}
