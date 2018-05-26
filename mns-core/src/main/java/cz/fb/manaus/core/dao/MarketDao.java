package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Market;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public class MarketDao extends GenericHibernateDao<Market, String> {

    public MarketDao() {
        super(Market.class);
    }

    @Transactional
    public void saveOrUpdate(Market market) {
        Session session = getSession();
        Market stored = session.get(Market.class, market.getId());
        if (stored != null) {
            market.setVersion(stored.getVersion());
            session.clear();
        }
        super.saveOrUpdate(market);
    }

    @Transactional
    public int deleteMarkets(Date olderThan) {
        var hql = getSession().getNamedQuery(Market.DELETE_OLDER_THAN);
        hql.setParameter("date", olderThan);
        return hql.executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<Market> getMarkets(Optional<Date> from, Optional<Date> to, OptionalInt maxResults) {

        var builder = entityManagerFactory.getCriteriaBuilder();
        var criteria = builder.createQuery(Market.class);
        var root = criteria.from(Market.class);
        Path<Date> openDate = root.get("event").get("openDate");
        criteria.orderBy(builder.asc(openDate), builder.asc(root.get("id")));

        var predicates = new LinkedList<Predicate>();
        from.ifPresent(val -> predicates.add(builder.greaterThanOrEqualTo(openDate, val)));
        to.ifPresent(val -> predicates.add(builder.lessThanOrEqualTo(openDate, val)));
        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        var query = getSession().createQuery(criteria);
        maxResults.ifPresent(query::setMaxResults);
        return query.getResultList();
    }

    @Transactional
    public void delete(String marketId) {
        getSession().delete(get(marketId).get());
    }
}
