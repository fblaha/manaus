package cz.fb.manaus.core.model

import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class BetActionTest {

    @Test
    fun `current actions`() {
        val currDate = Instant.now()
        val priceBack = Price(2.0, 2.0, Side.BACK)
        val priceLay = Price(1.8, 2.0, Side.LAY)
        val actionTemplates = betAction.copy(selectionId = 1L)
        val back1 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(10, ChronoUnit.HOURS),
                price = priceBack
        )
        val back2 = actionTemplates.copy(
                betActionType = BetActionType.UPDATE,
                time = currDate.minus(9, ChronoUnit.HOURS),
                price = priceBack
        )
        val back3 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(8, ChronoUnit.HOURS),
                price = priceBack
        )
        val lay1 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(6, ChronoUnit.HOURS),
                price = priceLay
        )
        val lay2 = actionTemplates.copy(
                betActionType = BetActionType.UPDATE,
                time = currDate.minus(5, ChronoUnit.HOURS),
                price = priceLay
        )
        val lay3 = actionTemplates.copy(
                betActionType = BetActionType.PLACE,
                time = currDate.minus(4, ChronoUnit.HOURS),
                price = priceLay
        )
        var filtered = getCurrentActions(listOf(back1, back2, back3))
        assertEquals(1, filtered.size)
        assertEquals(back3, filtered[filtered.size - 1])

        filtered = getCurrentActions(listOf(lay1, lay2))
        assertEquals(2, filtered.size)
        assertEquals(lay1, filtered[0])
        assertEquals(lay2, filtered[filtered.size - 1])

        filtered = getCurrentActions(listOf(lay1, lay2, lay3))
        assertEquals(1, filtered.size)
        assertEquals(lay3, filtered[filtered.size - 1])
    }
}