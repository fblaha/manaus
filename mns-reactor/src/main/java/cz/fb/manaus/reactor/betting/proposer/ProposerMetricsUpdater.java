package cz.fb.manaus.reactor.betting.proposer;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Joiner;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.reactor.betting.action.BetActionListener;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProposerMetricsUpdater implements BetActionListener {

    public static final String PROPOSER_METRIC = "proposer";
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private MetricRegistry metricRegistry;

    @Override
    public void onAction(BetAction action) {
        String proposers = action.getProperties().get(BetAction.PROPOSER_PROP);
        String side = action.getPrice().getSide().name().toLowerCase();
        for (String proposer : betUtils.parseProposers(proposers)) {
            String key = Joiner.on('.').join(PROPOSER_METRIC, side, proposer);
            metricRegistry.counter(key).inc();
        }
    }
}
