package cz.fb.manaus.core.dao;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.spring.ManausProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
@Profile(ManausProfiles.DB_PROFILE)
public class SettledBetDao extends GenericHibernateDao<SettledBet, Long> {

    public SettledBetDao() {
        super(SettledBet.class);
    }

    @Transactional(readOnly = true)
    public Optional<SettledBet> getSettledBet(String betId) {
        var builder = entityManagerFactory.getCriteriaBuilder();
        var criteria = builder.createQuery(SettledBet.class);
        var root = criteria.from(SettledBet.class);
        criteria.where(builder.equal(root.join("betAction").get("betId"), betId));
        return getSession().createQuery(criteria).uniqueResultOptional();
    }


    @Transactional(readOnly = true)
    public List<SettledBet> getSettledBets(String marketId, OptionalLong selectionId, Optional<Side> side) {
        var builder = entityManagerFactory.getCriteriaBuilder();
        var criteria = builder.createQuery(SettledBet.class);
        Root<SettledBet> root = criteria.from(SettledBet.class);

        var predicates = new LinkedList<Predicate>();
        predicates.add(builder.equal(root.join("betAction").join("market").get("id"), marketId));
        selectionId.ifPresent(val -> predicates.add(builder.equal(root.get("selectionId"), val)));
        side.ifPresent(val -> predicates.add(builder.equal(root.get("price").get("side"), val)));
        criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        return getSession().createQuery(criteria).getResultList();
    }


    @Transactional(readOnly = true)
    public List<SettledBet> getSettledBets(Optional<Date> from, Optional<Date> to, Optional<Side> side, OptionalInt maxResults) {
        var builder = entityManagerFactory.getCriteriaBuilder();
        var criteria = builder.createQuery(SettledBet.class);
        var root = criteria.from(SettledBet.class);

        criteria.orderBy(builder.desc(root.get("settled")));
        Path<Date> settled = root.get("settled");

        var predicates = new LinkedList<Predicate>();
        side.ifPresent(val -> predicates.add(builder.equal(root.get("price").get("side"), val)));
        from.ifPresent(val -> predicates.add(builder.greaterThanOrEqualTo(settled, val)));
        to.ifPresent(val -> predicates.add(builder.lessThanOrEqualTo(settled, val)));
        if (!predicates.isEmpty()) {
            criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }
        var query = getSession().createQuery(criteria);
        maxResults.ifPresent(query::setMaxResults);
        return query.getResultList();
    }


}
