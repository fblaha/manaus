package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.MarketPrices;
import cz.fb.manaus.core.model.RunnerPrices;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.OptionalInt;

@Repository
public class MarketPricesDao extends GenericHibernateDao<MarketPrices, Integer> {

    public MarketPricesDao() {
        super(MarketPrices.class);
    }

    @Transactional(readOnly = true)
    public List<MarketPrices> getPrices(String marketId, OptionalInt maxResults) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<MarketPrices> criteria = builder.createQuery(MarketPrices.class);
        Root<MarketPrices> root = criteria.from(MarketPrices.class);
        criteria.orderBy(builder.desc(root.get("time")));
        criteria.where(builder.equal(root.join("market").get("id"), marketId));
        Query<MarketPrices> query = getSession().createQuery(criteria);
        maxResults.ifPresent(val -> query.setMaxResults(val));
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
