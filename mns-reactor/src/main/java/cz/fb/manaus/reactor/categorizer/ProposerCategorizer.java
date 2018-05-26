package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProposerCategorizer extends AbstractProposerCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        var proposers = getProposers(settledBet);
        var builder = ImmutableSet.<String>builder();
        var side = settledBet.getPrice().getSide();
        for (var proposer : proposers) {
            builder.add(getSideAware("proposer_", side, proposer));
        }
        return builder.build();
    }

}
