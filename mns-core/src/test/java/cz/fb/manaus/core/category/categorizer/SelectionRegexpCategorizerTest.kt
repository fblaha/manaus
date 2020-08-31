package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class SelectionRegexpCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: SelectionRegexpCategorizer

    @Test
    fun `draw category`() {
        assertTrue { "selectionRegexp_draw" in categorizer.getCategories("The Draw") }
    }

}
