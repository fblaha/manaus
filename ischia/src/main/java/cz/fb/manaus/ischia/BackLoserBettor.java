package cz.fb.manaus.ischia;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.reactor.betting.listener.AbstractUpdatingBettor;
import cz.fb.manaus.reactor.betting.validator.Validator;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile(ManausProfiles.DB)
public class BackLoserBettor extends AbstractUpdatingBettor {

    @BackLoserBet
    @Autowired
    public BackLoserBettor(List<Validator> validators,
                           BackLoserAdviser coordinator) {
        super(Side.BACK, validators, coordinator);
    }

}
