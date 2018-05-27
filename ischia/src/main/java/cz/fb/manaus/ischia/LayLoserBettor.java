package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.PriceAdviser;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DatabaseComponent
public class LayLoserBettor extends AbstractUpdatingBettor {

    @LayLoserBet
    @Autowired
    public LayLoserBettor(List<Validator> validators,
                          PriceAdviser priceAdviser) {
        super(Side.LAY, validators, priceAdviser);
    }
}
