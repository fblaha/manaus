package cz.fb.manaus.reactor.betting.listener;

import cz.fb.manaus.core.model.Bet;
import cz.fb.manaus.core.model.Price;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.provider.ExchangeProvider;
import cz.fb.manaus.reactor.betting.AmountAdviser;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

abstract public class AbstractProposerBettor extends AbstractUpdatingBettor {

    @Autowired
    private AmountAdviser adviser;
    @Autowired
    private ExchangeProvider provider;

    public AbstractProposerBettor(Side side, List<Validator> validators, List<PriceProposer> proposers) {
        super(side, validators, proposers);
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

}
