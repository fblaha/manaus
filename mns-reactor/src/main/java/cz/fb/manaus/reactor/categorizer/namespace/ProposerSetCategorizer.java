package cz.fb.manaus.reactor.categorizer.namespace;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.categorizer.AbstractProposerCategorizer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ProposerSetCategorizer extends AbstractProposerCategorizer {

    @Override
    public Optional<String> getNamespace() {
        return Optional.of("proposerSet");
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        List<String> proposers = Ordering.natural().sortedCopy(getProposers(settledBet));
        Side side = settledBet.getPrice().getSide();
        String category = getSideAware("proposerSet_", side, Joiner.on('~').join(proposers));
        return Collections.singleton(category);
    }

}
