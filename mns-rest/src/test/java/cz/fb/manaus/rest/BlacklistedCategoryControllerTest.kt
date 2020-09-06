package cz.fb.manaus.rest

import cz.fb.manaus.core.model.BlacklistedCategory
import org.junit.Test
import java.time.Duration
import kotlin.time.ExperimentalTime


@ExperimentalTime
class BlacklistedCategoryControllerTest : AbstractControllerTest() {

    @Test
    fun blacklist() {
        val weak = BlacklistedCategory("weak.category", Duration.ofDays(20), -30.0)
        blacklistedCategoryRepository.save(weak)
        checkResponse("/blacklist", "weak.category")
    }
}