package cz.fb.manaus.rest

import org.junit.Test


class MarketFootprintControllerTest : AbstractControllerTest() {

    @Test
    fun `get by ID`() {
        createLiveMarket()
        checkResponse("/footprints/2", "Banik", "Sparta")
    }
}