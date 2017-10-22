package cz.fb.manaus.reactor.betting.listener;

import com.google.common.base.Joiner;
import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposalService;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.proposer.ProposedPrice;
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

abstract public class AbstractProposerBettor extends AbstractUpdatingBettor {
    private static final Logger log = Logger.getLogger(AbstractProposerBettor.class.getSimpleName());

    @Autowired
    private AmountAdviser adviser;
    @Autowired
    private ExchangeProvider provider;
    private final List<PriceProposer> proposers;
    @Autowired
    private PriceProposalService proposalService;
    @Autowired
    private RoundingService roundingService;

    public AbstractProposerBettor(Side side, List<Validator> validators, List<PriceProposer> proposers) {
        super(side, validators);
        this.proposers = proposers;
    }

    @Override
    protected Optional<Price> getNewPrice(BetContext betContext) {
        OptionalDouble proposedPrice = reducePrices(betContext);
        if (proposedPrice.isPresent()) {
            double amount = adviser.getAmount();
            Optional<Bet> counterBet = betContext.getCounterBet();
            if (counterBet.isPresent() && counterBet.get().getMatchedAmount() > 0) {
                amount = counterBet.get().getRequestedPrice().getAmount();
            }
            return Optional.of(new Price(proposedPrice.getAsDouble(), Math.max(amount, provider.getMinAmount()), side));
        } else {
            return Optional.empty();
        }
    }

    protected OptionalDouble reducePrices(BetContext context) {
        ProposedPrice proposedPrice = proposalService.reducePrices(context, proposers, context.getSide());
        OptionalDouble rounded = roundingService.roundBet(proposedPrice.getPrice());
        if (rounded.isPresent()) {
            context.getProperties().put(BetAction.PROPOSER_PROP, Joiner.on(',').join(proposedPrice.getProposers()));
        }
        return rounded;
    }

    @PostConstruct
    public void logConfig() {
        String proposerList = this.proposers.stream().map(Validator::getClass)
                .map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
        log.log(Level.INFO, "Bettor class: ''{0}'', proposers: ''{2}''",
                new Object[]{this.getClass().getSimpleName(), proposerList});
    }
}
