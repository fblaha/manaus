package cz.fb.manaus.reactor.profit.progress;

import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.profit.AbstractProfitTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Ordering.from;
import static java.util.Comparator.comparingDouble;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ProgressProfitServiceTest extends AbstractProfitTest {

    @Autowired
    private ProgressProfitService service;
    @Autowired
    private ExchangeProvider provider;


    @Test
    public void testSingleChunk() throws Exception {
        List<SettledBet> bets = generateBets(Optional.empty());
        List<ProfitRecord> records = service.getProfitRecords(bets,
                Optional.of("price"), 1, provider.getChargeRate(), Optional.empty());
        assertThat(records.size(), is(1));
        assertThat(records.get(0).getCategory(), is("price: 2.79"));
        assertThat(records.get(0).getTotalCount(), is(bets.size()));
        assertThat(records.get(0).getBackCount(), is(bets.size() / 2));
    }

    @Test
    public void testMultipleChunks() throws Exception {
        List<SettledBet> bets = generateBets(Optional.empty());
        List<ProfitRecord> records = service.getProfitRecords(bets,
                Optional.of("price"), 10, provider.getChargeRate(), Optional.empty());
        assertThat(records.size(), is(10));
        assertTrue(from(comparingDouble(ProfitRecord::getAvgPrice)).isStrictlyOrdered(records));
        records.forEach(System.out::println);
    }
}