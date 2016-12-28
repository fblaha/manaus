package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.junit.Assert.assertEquals;


public class AbstractBeforeCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private TestBeforeCategorizer categorizer;

    @Test
    public void testBeforeResolver() throws Exception {
        String cat = categorizer.getDayMap().get(2l);
        assertEquals("test_day_2-3", cat);
    }

    @Component
    private static class TestBeforeCategorizer extends AbstractBeforeCategorizer {

        protected TestBeforeCategorizer() {
            super("test");
        }

        @Override
        protected Date getDate(SettledBet settledBet) {
            return new Date();
        }

    }

}