package cz.fb.manaus.core.settlement;

import cz.fb.manaus.core.dao.AbstractDaoTest;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SettledBetSaverTest extends AbstractDaoTest {

    @Autowired
    private SettledBetSaver saver;

    @Test
    public void testSaver() throws Exception {
        createMarketWithSingleAction();
        assertThat(saver.saveBet(BET_ID, createAction()), is(SaveStatus.OK));
        assertThat(saver.saveBet(BET_ID, createAction()), is(SaveStatus.COLLISION));
        assertThat(saver.saveBet(BET_ID + "x", createAction()), is(SaveStatus.NO_ACTION));
    }

    private SettledBet createAction() {
        return new SettledBet(CoreTestFactory.DRAW, CoreTestFactory.DRAW_NAME, 5, new Date(), new Date(), new Price(3, 3, Side.LAY));
    }

}