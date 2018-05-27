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

import java.util.OptionalDouble;


@Component
@BackLoserBet
@LayLoserBet
@Profile("betfair")
public class TradedVolumeProposer implements PriceProposer {

    public static final double REDUCTION_RATE = 0.01;
    @Autowired
    private PriceService priceService;

    @Override
    public ValidationResult validate(BetContext context) {
        if (context.getActualTradedVolume().get().getVolume().size() == 0 || context.getActualTradedVolume().get().getWeightedMean().getAsDouble() > 100) {
            return ValidationResult.REJECT;
        } else {
            return PriceProposer.super.validate(context);
        }
    }

    @Override
    public OptionalDouble getProposedPrice(BetContext context) {
        double weightedMean = context.getActualTradedVolume().get().getWeightedMean().getAsDouble();
        return OptionalDouble.of(priceService.downgrade(weightedMean, REDUCTION_RATE, context.getSide()));
    }


}
