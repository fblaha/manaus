package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.category.categorizer.SettledBetCategorizer;
import cz.fb.manaus.core.dao.BetActionDao;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.betting.action.BetUtils;
import cz.fb.manaus.spring.DatabaseComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.Set;
import java.util.logging.Level;
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
        var market = settledBet.getBetAction().getMarket();
        var betActions = betActionDao.getBetActions(market.getId(),
                OptionalLong.of(settledBet.getSelectionId()), of(settledBet.getPrice().getSide()));
        if (betActions.isEmpty()) {
            log.log(Level.WARNING, "missing  bet actions ''{0}''", settledBet);
            return Set.of();
        }
        var current = betUtils.getCurrentActions(betActions);
        var result = new HashSet<String>();
        for (var categorizer : relatedActionsAwareCategorizers) {
            var partial = categorizer.getCategories(current, market);
            if (partial != null) {
                result.addAll(partial);
            }
        }
        return result;
    }
}





