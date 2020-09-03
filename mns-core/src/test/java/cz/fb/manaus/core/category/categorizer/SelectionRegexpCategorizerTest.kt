package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.test.AbstractTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class SelectionRegexpCategorizerTest : AbstractTestCase() {

    @Autowired
    private lateinit var categorizer: SelectionRegexpCategorizer

    @Test
    fun `draw category`() {
        assertTrue { "selectionRegexp_draw" in categorizer.getCategories("The Draw") }
    }

}
