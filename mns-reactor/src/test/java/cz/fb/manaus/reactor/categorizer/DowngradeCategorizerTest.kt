package cz.fb.manaus.reactor.categorizer

import cz.fb.manaus.core.model.*
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DowngradeCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DowngradeCategorizer

    @Test
    fun category() {
        val now = Instant.now()
        val place = betAction.copy(price = Price(2.0, 5.0, Side.LAY),
                time = now.minus(5, ChronoUnit.HOURS))
        var update = betAction.copy(betActionType = BetActionType.UPDATE,
                price = Price(2.1, 5.0, Side.LAY),
                time = now.minus(2, ChronoUnit.HOURS))

        assertEquals(emptySet(), categorizer.getCategories(listOf(place, update), market))

        update = update.copy(price = Price(1.9, 5.0, Side.LAY))
        assertThat(categorizer.getCategories(listOf(place, update), market), hasItems(DowngradeCategorizer.DOWNGRADE, DowngradeCategorizer.DOWNGRADE_LAST))


        val update2 = betAction.copy(betActionType = BetActionType.UPDATE,
                price = Price(2.1, 5.0, Side.LAY),
                time = now.minus(1, ChronoUnit.HOURS))

        assertEquals(setOf(DowngradeCategorizer.DOWNGRADE),
                categorizer.getCategories(listOf(place, update, update2), market))
    }

    @Test
    fun `actions mixed sides - illegal state`() {
        val now = Instant.now()
        val place = betAction.copy(price = Price(2.0, 5.0, Side.LAY),
                time = now.minus(5, ChronoUnit.HOURS))
        var update = betAction.copy(betActionType = BetActionType.UPDATE,
                price = Price(2.1, 5.0, Side.BACK),
                time = now.minus(2, ChronoUnit.HOURS))

        assertFailsWith<IllegalStateException> { categorizer.getCategories(listOf(place, update), market) }

    }

    @Test
    fun `actions unordered - illegal state`() {
        val now = Instant.now()
        val place = betAction.copy(price = Price(2.0, 5.0, Side.LAY),
                time = now.minus(5, ChronoUnit.HOURS))
        var update = betAction.copy(betActionType = BetActionType.UPDATE,
                price = Price(2.1, 5.0, Side.LAY),
                time = now.minus(7, ChronoUnit.HOURS))

        assertFailsWith<IllegalStateException> {
            categorizer.getCategories(listOf(place, update), market)
        }
    }
}
