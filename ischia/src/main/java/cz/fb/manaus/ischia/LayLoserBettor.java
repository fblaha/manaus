package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.PriceAdviser;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile(ManausProfiles.DB_PROFILE)
public class LayLoserBettor extends AbstractUpdatingBettor {

    @LayLoserBet
    @Autowired
    public LayLoserBettor(List<Validator> validators,
                          PriceAdviser priceAdviser) {
        super(Side.LAY, validators, priceAdviser);
    }
}
