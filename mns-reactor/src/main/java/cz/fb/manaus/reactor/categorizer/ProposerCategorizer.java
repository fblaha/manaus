package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class ProposerCategorizer extends AbstractProposerCategorizer {

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        List<String> proposers = getProposers(settledBet);
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        Side side = settledBet.getPrice().getSide();
        for (String proposer : proposers) {
            builder.add(getSideAware("proposer_", side, proposer));
        }
        return builder.build();
    }

}
