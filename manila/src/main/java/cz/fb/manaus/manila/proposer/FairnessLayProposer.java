package cz.fb.manaus.manila.proposer;

import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.manila.ManilaBet;
import cz.fb.manaus.reactor.betting.proposer.common.AbstractFairnessProposer;
import org.springframework.stereotype.Component;

@Component
@ManilaBet
public class FairnessLayProposer extends AbstractFairnessProposer {

    public FairnessLayProposer() {
        super(Side.LAY, ctx -> 0.07d);
    }
}
