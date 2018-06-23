package cz.fb.manaus.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class RunnerTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static Runner create(long selectionId, String name, double handicap, int sortPriority) {
        var runner = new Runner();
        runner.setSelectionId(selectionId);
        runner.setName(name);
        runner.setHandicap(handicap);
        runner.setSortPriority(sortPriority);
        return runner;
    }

    @Test
    public void testMarshallJson() throws Exception {
        Runner runner = create(100, "Sparta", 0, 0);
        String json = MAPPER.writeValueAsString(runner);
        assertThat(json, containsString("Sparta"));
    }

}