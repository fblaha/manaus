package cz.fb.manaus.core.repository

import cz.fb.manaus.core.model.BlacklistedCategory
import cz.fb.manaus.core.test.AbstractIntegrationTestCase
import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals

class BlacklistedCategoryRepositoryTest : AbstractIntegrationTestCase() {

    @Test
    fun `save - read`() {
        val category = BlacklistedCategory("test", Duration.ofDays(30), -30.0)
        blacklistedCategoryRepository.saveOrUpdate(category)
        assertEquals(category, blacklistedCategoryRepository.read(category.name))
    }
}