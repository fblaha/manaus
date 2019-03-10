package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.test.AbstractDatabaseTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertTrue

class UnprofitableCategoriesRefresherTest : AbstractDatabaseTestCase() {


    @Autowired
    private lateinit var refresher: UnprofitableCategoriesRefresher

    @Test
    fun cleanUp() {
        val entity = BlacklistedCategory("test", Duration.ofDays(30), -30.0)
        blacklistedCategoryRepository.save(entity)
        assertTrue(blacklistedCategoryRepository.list().isNotEmpty())
        refresher.execute()
        assertTrue(blacklistedCategoryRepository.list().isEmpty())
    }
}