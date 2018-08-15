package cz.fb.manaus.core.category.categorizer

import cz.fb.manaus.core.test.AbstractLocalTestCase
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class WeekDayCategorizerTest : AbstractLocalTestCase() {

    @Autowired
    private lateinit var categorizer: WeekDayCategorizer

    @Test
    fun testCategory() {
        val cal = Calendar.getInstance()
        cal.set(2012, Calendar.OCTOBER, 22, 9, 40)
        assertEquals(setOf("mon"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 23)
        assertEquals(setOf("tue"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 24)
        assertEquals(setOf("wed"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 25)
        assertEquals(setOf("thu"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 26)
        assertEquals(setOf("fri"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 27)
        assertEquals(setOf("sat"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 28)
        assertEquals(setOf("sun"), categorizer.getCategory(cal.time))
        cal.set(Calendar.DAY_OF_MONTH, 29)
        assertEquals(setOf("mon"), categorizer.getCategory(cal.time))
    }
}
