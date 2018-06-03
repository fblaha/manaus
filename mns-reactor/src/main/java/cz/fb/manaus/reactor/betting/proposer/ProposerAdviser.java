package cz.fb.manaus.reactor.betting.proposer;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.PriceAdviser;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.reactor.rounding.RoundingService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProposerAdviser implements PriceAdviser {

    private static final Logger log = Logger.getLogger(ProposerAdviser.class.getSimpleName());
    private final List<PriceProposer> proposers;

    @Autowired
    private AmountAdviser adviser;
    @Autowired
    private ExchangeProvider provider;
    @Autowired
    private PriceProposalService proposalService;
    @Autowired
    private RoundingService roundingService;

    public ProposerAdviser(List<PriceProposer> proposers) {
        this.proposers = proposers;
    }

    @Override
    public Optional<Price> getNewPrice(BetContext betContext) {
        var proposedPrice = reducePrices(betContext);
        if (proposedPrice.isPresent()) {
            var amount = adviser.getAmount();
            var counterBet = betContext.getCounterBet();
            if (counterBet.isPresent() && counterBet.get().getMatchedAmount() > 0) {
                amount = counterBet.get().getRequestedPrice().getAmount();
            }
            return Optional.of(new Price(proposedPrice.getAsDouble(),
                    Math.max(amount, provider.getMinAmount()), betContext.getSide()));
        } else {
            return Optional.empty();
        }
    }

    protected OptionalDouble reducePrices(BetContext context) {
        var proposedPrice = proposalService.reducePrices(context, proposers, context.getSide());
        var rounded = roundingService.roundBet(proposedPrice.getPrice());
        if (rounded.isPresent()) {
            context.getProperties().put(BetAction.PROPOSER_PROP, Joiner.on(',').join(proposedPrice.getProposers()));
        }
        return rounded;
    }

    @PostConstruct
    public void logConfig() {
        var proposerList = proposers.stream().map(Validator::getClass)
                .map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
        log.log(Level.INFO, "Proposer coordinator class: ''{0}'', proposers: ''{1}''",
                new Object[]{this.getClass().getSimpleName(), proposerList});
    }
}
