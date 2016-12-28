package cz.fb.manaus.rest;

import cz.fb.manaus.core.dao.AbstractDaoTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
abstract public class AbstractControllerTest extends AbstractDaoTest {
    protected MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    protected void checkResponse(String url, String... substrings) throws Exception {
        MvcResult result = mvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println("content = " + content);
        for (String substring : substrings) {
            assertThat(content, containsString(substring));
        }
    }

    @Before
    public void mockRest() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }


}
