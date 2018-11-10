package cz.fb.manaus.core.manager

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cz.fb.manaus.core.repository.domain.Market
import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.zip.ZipFile

abstract class AbstractMarketDataAwareTestCase : AbstractLocalTestCase() {
    protected lateinit var markets: List<Market>
    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Before
    fun setUp() {
        val resource = resourceLoader.getResource("classpath:cz/fb/manaus/core/service/markets.zip")
        val zipFile = ZipFile(resource.file)
        val zipEntry = zipFile.getEntry("markets.json")
        markets = ObjectMapper().readValue(zipFile.getInputStream(zipEntry), TYPE_REF)
        markets = markets.map { it.copy(event = it.event.copy(openDate = Instant.now().plus(5, ChronoUnit.HOURS))) }
    }

    companion object {
        val TYPE_REF: TypeReference<List<Market>> = object : TypeReference<List<Market>>() {}
    }

}
