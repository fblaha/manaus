package cz.fb.manaus.reactor.profit.progress;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.profit.AbstractProfitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CoverageFunctionProfitServiceTest extends AbstractProfitTest {

    @Autowired
    private CoverageFunctionProfitService service;
    @Autowired
    private ExchangeProvider provider;

    @Test
    public void testPriceCovered() {
        var bets = generateBets(Optional.empty());
        var records = service.getProfitRecords(bets,
                Optional.of("price"), provider.getChargeRate(), Optional.empty());
        assertThat(records.size(), is(2));
        assertThat(records.get(0).getCategory(), is("price_covered: 2.79"));
        assertThat(records.get(0).getTotalCount(), is(bets.size()));
        assertThat(records.get(0).getBackCount(), is(bets.size() / 2));
    }

    @Test
    public void testPriceSolo() {
        var bets = generateBets(Optional.of(Side.BACK));
        var records = service.getProfitRecords(bets,
                Optional.of("price"), provider.getChargeRate(), Optional.empty());
        assertThat(records.size(), is(1));
        assertThat(records.get(0).getCategory(), is("price_solo: 2.84"));
        assertThat(records.get(0).getTotalCount(), is(bets.size()));
        assertThat(records.get(0).getBackCount(), is(bets.size()));
    }

}