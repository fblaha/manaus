package cz.fb.manaus.reactor.betting.proposer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.price.PriceService;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;
import static java.util.Objects.requireNonNull;

@Service
public class PriceProposalService {

    public static final Ordering<ProposedPrice> ORDERING = Ordering.from(comparingDouble(ProposedPrice::getPrice));
    @Autowired
    private RoundingService roundingService;
    @Autowired
    private PriceService priceService;

    public ProposedPrice reducePrices(BetContext context, List<PriceProposer> proposers, Side side) {
        List<ProposedPrice> prices = new LinkedList<>();
        Preconditions.checkState(!proposers.isEmpty());
        for (PriceProposer proposer : proposers) {
            OptionalDouble proposedPrice = proposer.getProposedPrice(context);
            if (proposer.isMandatory()) {
                Preconditions.checkState(proposedPrice.isPresent(), proposer.getClass());
            }
            if (proposedPrice.isPresent()) {
                prices.add(new ProposedPrice(proposedPrice.getAsDouble(), proposer.getName()));
            }
        }
        return reduce(side, prices);
    }

    private ProposedPrice reduce(Side side, Collection<ProposedPrice> values) {
        ProposedPrice result;
        if (requireNonNull(side) == Side.BACK) {
            result = ORDERING.max(values);
        } else {
            result = ORDERING.min(values);
        }

        Set<String> proposers = values.stream()
                .filter(v -> Price.priceEq(v.getPrice(), result.getPrice()))
                .map(ProposedPrice::getProposers)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return new ProposedPrice(result.getPrice(), proposers);
    }

}
