package cz.fb.manaus.ischia;

import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@BackLoserBet
@Component
public class BackLoserAdviser extends ProposerAdviser {

    @BackLoserBet
    @Autowired
    public BackLoserAdviser(List<PriceProposer> proposers) {
        super(proposers);
    }
}
