package cz.fb.manaus.ischia;

import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@LayLoserBet
@Component
public class LayLoserAdviser extends ProposerAdviser {

    @LayLoserBet
    @Autowired
    public LayLoserAdviser(List<PriceProposer> proposers) {
        super(proposers);
    }
}
