package cz.fb.manaus.core.manager

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.model.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import java.time.Instant
import java.time.temporal.ChronoUnit

abstract class AbstractMarketDataAwareTestCase : AbstractLocalTestCase() {
    protected lateinit var markets: List<Market>
    @Autowired
    private lateinit var resourceLoader: ResourceLoader
    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Before
    fun setUp() {
        val resource = resourceLoader.getResource("classpath:cz/fb/manaus/core/service/markets.json")
        markets = objectMapper.readValue(resource.inputStream, TYPE_REF)
        markets = markets.map { it.copy(event = it.event.copy(openDate = Instant.now().plus(5, ChronoUnit.HOURS))) }
    }

    companion object {
        val TYPE_REF: TypeReference<List<Market>> = object : TypeReference<List<Market>>() {}
    }

}
