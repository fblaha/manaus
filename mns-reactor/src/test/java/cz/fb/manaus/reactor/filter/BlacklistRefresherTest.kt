package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertTrue

class BlacklistRefresherTest : AbstractIntegrationTestCase() {

    @Autowired
    private lateinit var refresher: BlacklistRefresher

    @Test
    fun cleanUp() {
        val entity = BlacklistedCategory("test", Duration.ofDays(30), -30.0)
        blacklistedCategoryRepository.save(entity)
        assertTrue(blacklistedCategoryRepository.list().isNotEmpty())
        refresher.execute()
        assertTrue(blacklistedCategoryRepository.list().isEmpty())
    }
}