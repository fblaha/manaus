package cz.fb.manaus.manila;

import cz.fb.manaus.reactor.betting.proposer.PriceProposer;
import cz.fb.manaus.reactor.betting.proposer.ProposerAdviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@ManilaBet
@Component
public class BestChanceLayAdviser extends ProposerAdviser {

    @ManilaBet
    @Autowired
    public BestChanceLayAdviser(List<PriceProposer> proposers) {
        super(proposers);
    }
}
