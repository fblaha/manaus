package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
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
import java.util.OptionalLong;

@Repository
public class SettledBetDao extends GenericHibernateDao<SettledBet, Long> {

    public SettledBetDao() {
        super(SettledBet.class);
    }

    @Transactional(readOnly = true)
    public Optional<SettledBet> getSettledBet(String betId) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<SettledBet> criteria = builder.createQuery(SettledBet.class);
        Root<SettledBet> root = criteria.from(SettledBet.class);
        criteria.where(builder.equal(root.join("betAction").get("betId"), betId));
        return getSession().createQuery(criteria).uniqueResultOptional();
    }


    @Transactional(readOnly = true)
    public List<SettledBet> getSettledBets(String marketId, OptionalLong selectionId, Optional<Side> side) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<SettledBet> criteria = builder.createQuery(SettledBet.class);
        Root<SettledBet> root = criteria.from(SettledBet.class);

        List<Predicate> predicates = new LinkedList<>();
        predicates.add(builder.equal(root.join("betAction").join("market").get("id"), marketId));
        selectionId.ifPresent(val -> predicates.add(builder.equal(root.get("selectionId"), val)));
        side.ifPresent(val -> predicates.add(builder.equal(root.get("price").get("side"), val)));
        criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        return getSession().createQuery(criteria).getResultList();
    }


    @Transactional(readOnly = true)
    public List<SettledBet> getSettledBets(Optional<Date> from, Optional<Date> to, Optional<Side> side, OptionalInt maxResults) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<SettledBet> criteria = builder.createQuery(SettledBet.class);
        Root<SettledBet> root = criteria.from(SettledBet.class);

        criteria.orderBy(builder.desc(root.get("settled")));
        Path<Date> settled = root.get("settled");

        List<Predicate> predicates = new LinkedList<>();
        side.ifPresent(val -> predicates.add(builder.equal(root.get("price").get("side"), val)));
        from.ifPresent(val -> predicates.add(builder.greaterThanOrEqualTo(settled, val)));
        to.ifPresent(val -> predicates.add(builder.lessThanOrEqualTo(settled, val)));
        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        Query<SettledBet> query = getSession().createQuery(criteria);
        maxResults.ifPresent(query::setMaxResults);
        return query.getResultList();
    }


}
