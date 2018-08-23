package cz.fb.manaus.rest

import cz.fb.manaus.core.dao.AbstractDaoTest
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import kotlin.test.assertTrue

@WebAppConfiguration
abstract class AbstractControllerTest : AbstractDaoTest() {
    protected lateinit var mvc: MockMvc
    @Autowired
    private lateinit var context: WebApplicationContext

    protected fun checkResponse(url: String, vararg substrings: String) {
        val result = mvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
        val content = result.response.contentAsString
        for (substring in substrings) {
            assertTrue(substring in content)
        }
    }

    @Before
    fun mockRest() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
    }
}
