package cz.fb.manaus.ischia.proposer;

import cz.fb.manaus.ischia.BackLoserBet;
import cz.fb.manaus.ischia.LayLoserBet;
import cz.fb.manaus.reactor.betting.BetContext;
import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.validator.ValidationResult;
import cz.fb.manaus.reactor.price.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.OptionalDouble;

import static java.util.Optional.ofNullable;


@Component
@BackLoserBet
@LayLoserBet
@Profile("betfair")
public class LastMatchedProposer implements PriceProposer {

    @Autowired
    private PriceService priceService;

    @Override
    public ValidationResult validate(BetContext context) {
        var lastMatchedPrice = ofNullable(context.getRunnerPrices().getLastMatchedPrice());
        return ValidationResult.of(lastMatchedPrice.isPresent() && getProposedPrice(context).isPresent());
    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        var lastMatchedPrice = context.getRunnerPrices().getLastMatchedPrice();
        return OptionalDouble.of(priceService.downgrade(lastMatchedPrice,
                TradedVolumeProposer.REDUCTION_RATE, context.getSide()));
    }

}
