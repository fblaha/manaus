package cz.fb.manaus.core.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import cz.fb.manaus.core.model.BetAction;
import cz.fb.manaus.core.model.Side;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Ordering.from;
import static java.util.Comparator.comparing;

@Repository
public class BetActionDao extends GenericHibernateDao<BetAction, Integer> {

    public BetActionDao() {
        super(BetAction.class);
    }

    @Transactional(readOnly = true)
    public List<BetAction> getBetActions(String marketId, OptionalLong selId, Optional<Side> side) {
        return listActionQuery(marketId, selId, side, BetAction.class,
                Optional.empty(), Optional.of("actionDate"));
    }

    @Transactional
    public int updateBetId(String oldOne, String newOne, String marketId, long selectionId) {
        Query query = getSession().getNamedQuery(BetAction.SET_BET_ID);
        query.setParameter("newOne", newOne);
        query.setParameter("oldOne", oldOne);
        query.setParameter("selectionId", selectionId);
        query.setParameter("marketId", marketId);
        return query.executeUpdate();
    }

    @Transactional(readOnly = true)
    public Set<String> getBetActionIds(String marketId, OptionalLong selId, Optional<Side> side) {
        List<String> result = listActionQuery(marketId, selId, side, String.class,
                Optional.of("betId"), Optional.empty());
        return ImmutableSet.copyOf(result);
    }

    private <E> List<E> listActionQuery(String marketId, OptionalLong selId, Optional<Side> side,
                                        Class<E> clazz, Optional<String> field, Optional<String> orderField) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<E> criteria = builder.createQuery(clazz);
        Root<BetAction> root = criteria.from(BetAction.class);

        orderField.ifPresent(val -> criteria.orderBy(builder.asc(root.get(val))));
        field.ifPresent(val -> criteria.select(root.get(val)));

        List<Predicate> predicates = new LinkedList<>();
        predicates.add(builder.equal(root.join("market").get("id"), marketId));
        selId.ifPresent(val -> predicates.add(builder.equal(root.get("selectionId"), val)));
        side.ifPresent(val -> predicates.add(builder.equal(root.get("price").get("side"), val)));
        criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        return getSession().createQuery(criteria).getResultList();
    }

    @Transactional(readOnly = true)
    public List<BetAction> getBetActions(OptionalInt maxResults) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<BetAction> criteriaQuery = builder.createQuery(BetAction.class);
        Root<BetAction> root = criteriaQuery.from(BetAction.class);

        criteriaQuery.select(root);
        criteriaQuery.orderBy(builder.desc(root.get("actionDate")));

        TypedQuery<BetAction> query = getSession().createQuery(criteriaQuery);
        maxResults.ifPresent(val -> query.setMaxResults(val));
        List<BetAction> result = query.getResultList();
        checkState(from(comparing(BetAction::getActionDate)).reverse().isOrdered(result));
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<BetAction> getBetAction(String betId) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<BetAction> criteriaQuery = builder.createQuery(BetAction.class);
        Root<BetAction> actionRoot = criteriaQuery.from(BetAction.class);
        criteriaQuery.where(builder.equal(actionRoot.get("betId"), betId));
        Query<BetAction> query = getSession().createQuery(criteriaQuery);
        return query.uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public void fetchMarketPrices(BetAction action) {
        getSession().refresh(Preconditions.checkNotNull(action, "action==null"));
        Hibernate.initialize(action.getMarketPrices());
        action.setMarketPrices(clearProxy(action.getMarketPrices()));
    }

    @Transactional(readOnly = true)
    public void fetchMarketPrices(Stream<BetAction> actions) {
        actions.forEach(this::fetchMarketPrices);
    }

    @Transactional(readOnly = true)
    public Optional<Date> getBetActionDate(String betId) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Date> criteriaQuery = builder.createQuery(Date.class);
        Root<BetAction> actionRoot = criteriaQuery.from(BetAction.class);
        criteriaQuery.where(builder.equal(actionRoot.get("betId"), betId));
        criteriaQuery.select(actionRoot.get("actionDate"));
        Query<Date> sessionQuery = getSession().createQuery(criteriaQuery);
        return sessionQuery.uniqueResultOptional();
    }

}
