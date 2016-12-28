package cz.fb.manaus.reactor.price;

import cz.fb.manaus.core.model.Side;
import org.junit.Test;

import java.util.Optional;

import static java.util.OptionalDouble.empty;
import static java.util.OptionalDouble.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FairnessTest {

    @Test
    public void testMoreCredibleSide() throws Exception {
        assertThat(new Fairness(of(0.9), of(1.2)).getMoreCredibleSide().get(), is(Side.BACK));
        assertThat(new Fairness(of(0.9), of(1.05)).getMoreCredibleSide().get(), is(Side.LAY));

        assertThat(new Fairness(of(0.9), empty()).getMoreCredibleSide().get(), is(Side.BACK));
        assertThat(new Fairness(empty(), of(1.2)).getMoreCredibleSide().get(), is(Side.LAY));

        assertThat(new Fairness(empty(), empty()).getMoreCredibleSide(), is(Optional.empty()));
    }
}
