package cz.fb.manaus.reactor.categorizer

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cz.fb.manaus.core.model.BetAction
import cz.fb.manaus.core.model.Price
import cz.fb.manaus.core.model.Side
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.core.test.CoreTestFactory
import org.apache.commons.lang3.time.DateUtils.addHours
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DowngradeCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: DowngradeCategorizer

    @Test
    fun category() {
        val market = CoreTestFactory.newTestMarket()
        val place = mock<BetAction>()
        whenever(place.price).thenReturn(Price(2.0, 5.0, Side.LAY))
        val curr = Date()
        whenever(place.actionDate).thenReturn(addHours(curr, -5))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(Price(2.1, 5.0, Side.LAY))
        whenever(update.actionDate).thenReturn(addHours(curr, -2))

        assertEquals(emptySet(), categorizer.getCategories(listOf(place, update), market))

        whenever(update.price).thenReturn(Price(1.9, 5.0, Side.LAY))
        assertThat(categorizer.getCategories(listOf(place, update), market), hasItems(DowngradeCategorizer.DOWNGRADE, DowngradeCategorizer.DOWNGRADE_LAST))


        val update2 = mock<BetAction>()
        whenever(update2.price).thenReturn(Price(2.1, 5.0, Side.LAY))
        whenever(update2.actionDate).thenReturn(addHours(curr, -1))
        assertEquals(setOf(DowngradeCategorizer.DOWNGRADE), categorizer.getCategories(listOf(place, update, update2),
                CoreTestFactory.newTestMarket()))
    }

    @Test
    fun `actions mixed sides - illegal state`() {
        val market = CoreTestFactory.newTestMarket()

        val place = mock<BetAction>()
        whenever(place.price).thenReturn(Price(2.0, 5.0, Side.LAY))
        val curr = Date()
        whenever(place.actionDate).thenReturn(addHours(curr, -5))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(Price(2.1, 5.0, Side.BACK))
        whenever(update.actionDate).thenReturn(addHours(curr, -2))

        assertFailsWith<IllegalStateException> { categorizer.getCategories(listOf(place, update), market) }

    }

    @Test
    fun `actions unordered - illegal state`() {
        val place = mock<BetAction>()
        val price = Price(2.0, 5.0, Side.LAY)
        whenever(place.price).thenReturn(price)
        val curr = Date()
        whenever(place.actionDate).thenReturn(addHours(curr, -1))
        val update = mock<BetAction>()
        whenever(update.price).thenReturn(price)
        whenever(update.actionDate).thenReturn(addHours(curr, -2))
        assertFailsWith<IllegalStateException> {
            categorizer.getCategories(listOf(place, update),
                    CoreTestFactory.newTestMarket())
        }
    }
}
