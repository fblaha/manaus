package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.Optional.of;

@DatabaseComponent
public class RelatedActionsCategorizer implements SettledBetCategorizer {

    private static final Logger log = Logger.getLogger(RelatedActionsCategorizer.class.getSimpleName());


    @Autowired
    private BetActionDao betActionDao;
    @Autowired
    private BetUtils betUtils;
    @Autowired
    private List<RelatedActionsAwareCategorizer> relatedActionsAwareCategorizers;

    @Override
    public boolean isSimulationSupported() {
        return false;
    }

    @Override
    public Set<String> getCategories(SettledBet settledBet, BetCoverage coverage) {
        Market market = settledBet.getBetAction().getMarket();
        List<BetAction> betActions = betActionDao.getBetActions(market.getId(),
                OptionalLong.of(settledBet.getSelectionId()), of(settledBet.getPrice().getSide()));
        List<BetAction> current = betUtils.getCurrentActions(betActions);
        Set<String> result = new HashSet<>();
        for (RelatedActionsAwareCategorizer categorizer : relatedActionsAwareCategorizers) {
            Set<String> partial = categorizer.getCategories(current, market);
            if (partial != null) {
                result.addAll(partial);
            }
        }
        return result;
    }
}




