package cz.fb.manaus.reactor.rounding;

import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RoundingServiceTest extends AbstractLocalTestCase {
    public static final double START = 1.01d;
    public static final int REPEAT = 200;
    @Autowired
    protected RoundingService service;

    @Test
    public void testIncrementDecrement() throws Exception {
        double price = START;
        List<Double> upList = new LinkedList<>();
        List<Double> downList = new LinkedList<>();
        for (int i = 0; i < REPEAT; i++) {
            price = service.increment(price, 1).getAsDouble();
            upList.add(price);
        }
        price = service.increment(price, 1).getAsDouble();
        for (int i = 0; i < REPEAT; i++) {
            price = service.decrement(price, 1).getAsDouble();
            downList.add(price);
        }
        price = service.decrement(price, 1).getAsDouble();
        assertThat(price, is(START));
        Collections.reverse(downList);
        assertThat(upList, is(downList));
    }

    @Test
    public void testSteps() throws Exception {
        double price1 = START;
        for (int i = 0; i < REPEAT; i++) {
            price1 = service.increment(price1, 1).getAsDouble();
        }
        double price2 = START;
        for (int i = 0; i < REPEAT / 50; i++) {
            price2 = service.increment(price2, 50).getAsDouble();
        }
        assertThat(price1, is(price2));

        for (int i = 0; i < REPEAT; i++) {
            price1 = service.decrement(price1, 1).getAsDouble();
        }
        for (int i = 0; i < REPEAT / 50; i++) {
            price2 = service.decrement(price2, 50).getAsDouble();
        }
        assertThat(price1, is(price2));
    }

    @Test
    public void testRoundBet() throws Exception {
        assertThat(service.roundBet(1.0544444444d).getAsDouble(), is(1.05d));
        assertThat(service.roundBet(1.05555555d).getAsDouble(), is(1.06d));
        assertThat(service.roundBet(2.081).getAsDouble(), is(2.08d));
        assertThat(service.roundBet(2.09).getAsDouble(), is(2.1d));
        assertThat(service.roundBet(3.575).getAsDouble(), is(3.6d));
        assertThat(service.roundBet(3.84).getAsDouble(), is(3.85d));
        assertThat(service.roundBet(3.81).getAsDouble(), is(3.80d));
        assertThat(service.roundBet(3.83).getAsDouble(), is(3.85d));
        assertThat(service.roundBet(5.15).getAsDouble(), is(5.2d));
        assertThat(service.roundBet(5.14).getAsDouble(), is(5.1d));
        assertThat(service.roundBet(8.7).getAsDouble(), is(8.8d));
        assertThat(service.roundBet(8.69).getAsDouble(), is(8.6d));
        assertThat(service.roundBet(984).getAsDouble(), is(980d));
        assertThat(service.roundBet(985).getAsDouble(), is(990d));
    }

    @Test
    public void testRoundBet2() throws Exception {
        double price = START;
        for (int i = 0; i < REPEAT; i++) {
            price = service.increment(price, 1).getAsDouble();
            double oneMore = service.increment(price, 1).getAsDouble();
            double step = oneMore - price;
            assertThat(service.roundBet(price).getAsDouble(), is(price));
            assertThat(service.roundBet(price + step / 4d).getAsDouble(), is(price));
            assertThat(service.roundBet(price + 3d * step / 4d).getAsDouble(), is(oneMore));
        }
    }

}
