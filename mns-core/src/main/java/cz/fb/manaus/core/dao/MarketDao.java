package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.Market;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        Query hql = getSession().getNamedQuery(Market.DELETE_OLDER_THAN);
        hql.setParameter("date", olderThan);
        return hql.executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<Market> getMarkets(Optional<Date> from, Optional<Date> to, OptionalInt maxResults) {

        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Market> criteria = builder.createQuery(Market.class);
        Root<Market> root = criteria.from(Market.class);
        Path<Date> openDate = root.get("event").get("openDate");
        criteria.orderBy(builder.asc(openDate), builder.asc(root.get("id")));

        List<Predicate> predicates = new LinkedList<>();
        from.ifPresent(val -> predicates.add(builder.greaterThanOrEqualTo(openDate, val)));
        to.ifPresent(val -> predicates.add(builder.lessThanOrEqualTo(openDate, val)));
        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }

        Query<Market> query = getSession().createQuery(criteria);
        maxResults.ifPresent(val -> query.setMaxResults(val));
        return query.getResultList();
    }

    @Transactional
    public void delete(String marketId) {
        getSession().delete(get(marketId).get());
    }
}
