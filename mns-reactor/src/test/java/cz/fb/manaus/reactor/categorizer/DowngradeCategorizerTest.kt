package cz.fb.manaus.reactor.categorizer

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.model.market
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
        val market = market
        val place = mock<BetAction>()
        whenever(place.price).thenReturn(Price(2.0, 5.0, Side.LAY))
        val now = Instant.now()
        whenever(place.time).thenReturn(now.minus(5, ChronoUnit.HOURS))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(Price(2.1, 5.0, Side.LAY))
        whenever(update.time).thenReturn(now.minus(2, ChronoUnit.HOURS))

        assertEquals(emptySet(), categorizer.getCategories(listOf(place, update), market))

        whenever(update.price).thenReturn(Price(1.9, 5.0, Side.LAY))
        assertThat(categorizer.getCategories(listOf(place, update), market), hasItems(DowngradeCategorizer.DOWNGRADE, DowngradeCategorizer.DOWNGRADE_LAST))


        val update2 = mock<BetAction>()
        whenever(update2.price).thenReturn(Price(2.1, 5.0, Side.LAY))
        whenever(update2.time).thenReturn(now.minus(1, ChronoUnit.HOURS))
        assertEquals(setOf(DowngradeCategorizer.DOWNGRADE),
                categorizer.getCategories(listOf(place, update, update2),
                        market))
    }

    @Test
    fun `actions mixed sides - illegal state`() {
        val market = market

        val place = mock<BetAction>()
        whenever(place.price).thenReturn(Price(2.0, 5.0, Side.LAY))
        val now = Instant.now()
        whenever(place.time).thenReturn(now.minus(5, ChronoUnit.HOURS))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(Price(2.1, 5.0, Side.BACK))
        whenever(update.time).thenReturn(now.minus(5, ChronoUnit.HOURS))

        assertFailsWith<IllegalStateException> { categorizer.getCategories(listOf(place, update), market) }

    }

    @Test
    fun `actions unordered - illegal state`() {
        val place = mock<BetAction>()
        val price = Price(2.0, 5.0, Side.LAY)
        whenever(place.price).thenReturn(price)
        val now = Instant.now()
        whenever(place.time).thenReturn(now.minus(1, ChronoUnit.HOURS))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(price)
        whenever(update.time).thenReturn(now.minus(2, ChronoUnit.HOURS))
        assertFailsWith<IllegalStateException> {
            categorizer.getCategories(listOf(place, update), market)
        }
    }
}
