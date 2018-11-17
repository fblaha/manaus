package cz.fb.manaus.reactor.profit

import cz.fb.manaus.core.provider.ExchangeProvider
import cz.fb.manaus.core.test.AbstractLocalTestCase
import cz.fb.manaus.spring.ManausProfiles.Companion.TEST
import org.junit.Assert.assertEquals
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(value = ["matchbook", TEST], inheritProfiles = false)
class MatchbookProfitPluginTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var plugin: MatchbookProfitPlugin
    @Autowired
    private lateinit var provider: ExchangeProvider

    @Test
    fun `charge back win`() {
        assertEquals(0.207, plugin.getCharge(provider.chargeRate, 27.6, 10.0), 0.001)
        assertEquals(0.0381, plugin.getCharge(provider.chargeRate, 5.08, 2.0), 0.001)
    }

    @Test
    fun `charge back loss`() {
        assertEquals(0.015, plugin.getCharge(provider.chargeRate, -2.0, 2.0), 0.0001)
        assertEquals(0.075, plugin.getCharge(provider.chargeRate, -10.0, 10.0), 0.0001)
    }

    @Test
    fun `charge lay win`() {
        assertEquals(0.015, plugin.getCharge(provider.chargeRate, 2.0, 2.0), 0.001)
        assertEquals(0.075, plugin.getCharge(provider.chargeRate, 10.0, 10.0), 0.001)
    }

    @Test
    fun `charge lay loss`() {
        assertEquals(0.015, plugin.getCharge(provider.chargeRate, -4.68, 2.0), 0.0001)
        assertEquals(0.06, plugin.getCharge(provider.chargeRate, -8.0, 10.0), 0.0001)
    }

}