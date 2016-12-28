package cz.fb.manaus.core.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class PriceTest {

    @Test
    public void testEq() throws Exception {
        assertThat(new Price(2.28, 2.24, Side.LAY), equalTo(new Price(2.28, 2.24, Side.LAY)));
        assertThat(new Price(2.28, 2.04, Side.LAY), not(equalTo(new Price(2.28, 2.24, Side.LAY))));
    }
}
