package cz.fb.manaus.rest

import org.hamcrest.CoreMatchers
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ContextConfiguration(classes = [MaintenanceController::class])
class MaintenanceControllerTest : AbstractControllerTest() {

    @Test
    fun tasks() {
        checkResponse("/maintenance", "testTask", "777")
    }

    @Test
    fun `run task`() {
        val result = mvc.perform(post(
                "/maintenance/{name}", "testTask")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
        val content = result.response.contentAsString
        assertThat(content, CoreMatchers.containsString("test_delete"))
    }

    @Test
    fun `run task - not found`() {
        mvc.perform(post(
                "/maintenance/{name}", "missingTask")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
                .andReturn()
    }
}