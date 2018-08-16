package cz.fb.manaus.core.model

import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.test.CoreTestFactory
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*

class SettledBetTest {

    @Test
    fun `json marshall`() {
        val original = SettledBetFactory.create(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME,
                5.0, Date(), Price(5.0, 3.0, Side.BACK))

        val serialized = ObjectMapper().writer().writeValueAsString(original)
        val restored = ObjectMapper().readerFor(SettledBet::class.java).readValue<SettledBet>(serialized)
        assertThat(restored.price, `is`(original.price))
        assertThat(restored.settled, `is`(original.settled))
    }

}