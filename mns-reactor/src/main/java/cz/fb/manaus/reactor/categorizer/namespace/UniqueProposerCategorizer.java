package cz.fb.manaus.reactor.categorizer.namespace;

import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.categorizer.AbstractProposerCategorizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class UniqueProposerCategorizer extends AbstractProposerCategorizer {

    @Override
    public Optional<String> getNamespace() {
        return Optional.of("uniqueProposer");
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        List<String> proposers = getProposers(settledBet);
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        Side side = settledBet.getPrice().getSide();
        for (String proposer : proposers) {
            builder.add(getSideAware("uniqueProposer_", side, proposer + proposers.size()));
        }
        return builder.build();
    }

}
