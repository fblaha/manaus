package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DatabaseComponent
public class BackLoserBettor extends AbstractUpdatingBettor {

    @BackLoserBet
    @Autowired
    public BackLoserBettor(List<Validator> validators,
                           BackLoserAdviser coordinator) {
        super(Side.BACK, validators, coordinator);
    }

}
