package cz.fb.manaus.core.service;

import com.google.common.base.Strings;
import cz.fb.manaus.core.model.Property;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

@Repository
public class PropertiesService {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Transactional(readOnly = true)
    public Optional<String> get(String name) {
        Optional<Property> property = Optional.ofNullable(getSession().get(Property.class, name));
        if (property.isPresent() &&
                property.get().getExpiryDate().after(new Date())) {
            return Optional.of(property.get().getValue());
        }
        return Optional.empty();
    }

    @Transactional
    public void set(String name, String value, Duration validPeriod) {
        Date expiryDate = Date.from(Instant.now().plus(validPeriod));
        set(new Property(name, value, expiryDate));
    }

    @Transactional
    public void set(Property property) {
        Session session = getSession();
        if (session.get(Property.class, property.getName()) != null) {
            session.merge(property);
        } else {
            session.saveOrUpdate(property);
        }
    }

    @Transactional(readOnly = true)
    public List<Property> list(Optional<String> prefix) {
        Session session = getSession();
        Query<Property> namedQuery = session.getNamedQuery(Property.LIST_PROPERTIES);
        namedQuery.setParameter("prefix", prefix.orElse("") + "%");
        namedQuery.setParameter("current", new Date());
        return namedQuery.list();
    }

    private Session getSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).getCurrentSession();
    }

    @Transactional(readOnly = false)
    public int delete(String prefix) {
        Query delete = getSession().getNamedQuery(Property.DELETE_PREFIX);
        prefix = Strings.nullToEmpty(prefix);
        delete.setParameter("prefix", prefix + "%");
        return delete.executeUpdate();
    }

    @Transactional(readOnly = false)
    public int purgeExpired() {
        Query delete = getSession().getNamedQuery(Property.DELETE_EXPIRED);
        delete.setParameter("current", new Date());
        return delete.executeUpdate();
    }

    @Transactional
    public void setInstant(String name, Instant instant, Duration validPeriod) {
        setInstant(name, instant, validPeriod, ZoneId.systemDefault());
    }

    void setInstant(String name, Instant instant, Duration validPeriod, ZoneId zone) {
        DateTimeFormatter withZone = FORMATTER.withZone(zone);
        set(name, withZone.format(instant), validPeriod);
    }

    @Transactional(readOnly = true)
    public Optional<Instant> getInstant(String name) {
        Optional<String> stringDate = get(name);
        if (stringDate.isPresent()) {
            return Optional.of(Instant.from(FORMATTER.parse(stringDate.get())));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public void setLong(String name, long number, Duration validPeriod) {
        set(name, Long.toString(number), validPeriod);
    }

    @Transactional
    public long incrementAntGet(String name, Duration validPeriod) {
        OptionalLong stored = getLong(name);
        long val = 0;
        if (stored.isPresent()) {
            val = stored.getAsLong();
        }
        val++;
        setLong(name, val, validPeriod);
        return val;
    }

    @Transactional(readOnly = true)
    public OptionalLong getLong(String name) {
        Optional<String> stringLong = get(name);
        if (stringLong.isPresent()) {
            return OptionalLong.of(Long.parseLong(stringLong.get()));
        } else {
            return OptionalLong.empty();
        }
    }

    @Transactional
    public void setDouble(String name, double number, Duration validPeriod) {
        set(name, Double.toString(number), validPeriod);
    }

    @Transactional(readOnly = true)
    public OptionalDouble getDouble(String name) {
        Optional<String> stringDouble = get(name);
        if (stringDouble.isPresent()) {
            return OptionalDouble.of(Long.parseLong(stringDouble.get()));
        } else {
            return OptionalDouble.empty();
        }
    }
}
