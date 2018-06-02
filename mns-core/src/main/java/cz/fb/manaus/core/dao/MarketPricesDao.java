package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import cz.fb.manaus.spring.ManausProfiles;
import org.hibernate.query.Query;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalInt;

@Repository
@Profile(ManausProfiles.DB)
public class MarketPricesDao extends GenericHibernateDao<MarketPrices, Integer> {

    public MarketPricesDao() {
        super(MarketPrices.class);
    }

    @Transactional(readOnly = true)
    public List<MarketPrices> getPrices(String marketId, OptionalInt maxResults) {
        var builder = entityManagerFactory.getCriteriaBuilder();
        var criteria = builder.createQuery(MarketPrices.class);
        var root = criteria.from(MarketPrices.class);
        criteria.orderBy(builder.desc(root.get("time")));
        criteria.where(builder.equal(root.join("market").get("id"), marketId));
        var query = getSession().createQuery(criteria);
        maxResults.ifPresent(query::setMaxResults);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<MarketPrices> getPrices(String marketId) {
        return getPrices(marketId, OptionalInt.empty());
    }

    @Transactional(readOnly = true)
    public List<RunnerPrices> getRunnerPrices(String marketId, long selectionId, OptionalInt maxResults) {
        Query<RunnerPrices> namedQuery = getSession().getNamedQuery(RunnerPrices.BY_MARKET_AND_SELECTION);
        if (maxResults.isPresent()) {
            namedQuery.setMaxResults(maxResults.getAsInt());
        }
        namedQuery.setParameter("marketId", marketId);
        namedQuery.setParameter("selectionId", selectionId);
        return namedQuery.list();
    }


}
