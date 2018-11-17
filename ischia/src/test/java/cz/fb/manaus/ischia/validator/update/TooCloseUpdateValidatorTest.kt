package cz.fb.manaus.ischia.validator.update

import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.reactor.ReactorTestFactory
import cz.fb.manaus.reactor.betting.validator.ValidationResult
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals


@ActiveProfiles("ischia")
class TooCloseUpdateValidatorTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var validator: TooCloseUpdateValidator
    @Autowired
    private lateinit var factory: ReactorTestFactory

    @Test
    fun validate() {
        val context = factory.newBetContext(Side.BACK, 2.5, 3.5)
        assertEquals(ValidationResult.ACCEPT, validator.validate(context))
    }

}