package cz.fb.manaus.reactor.categorizer;

import com.google.common.base.MoreObjects;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.emptyToNull;

public abstract class AbstractProposerCategorizer implements SettledBetCategorizer {
    @Autowired
    private BetUtils betUtils;

    protected List<String> getProposers(SettledBet settledBet) {
        Map<String, String> properties = settledBet.getBetAction().getProperties();
        String rawProposers = MoreObjects.firstNonNull(emptyToNull(properties.get(BetAction.PROPOSER_PROP)), "none");

        return betUtils.parseProposers(rawProposers);
    }

    protected String getSideAware(String prefix, Side side, String category) {
        return prefix + on('.').join(side.name().toLowerCase(), category);
    }
}
