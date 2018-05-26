package cz.fb.manaus.core.dao;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.Optional;

public abstract class GenericHibernateDao<T, ID extends Serializable> {

    private final Class<T> persistentClass;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    public GenericHibernateDao(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    protected Session getSession() {
        return entityManagerFactory.unwrap(SessionFactory.class).getCurrentSession();
    }

    @Transactional(readOnly = true)
    public Optional<T> get(ID id) {
        return Optional.ofNullable(getSession().get(persistentClass, id));
    }

    @Transactional
    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }

    @Transactional
    public void delete(T entity) {
        getSession().delete(entity);
    }

    protected <T> T clearProxy(T proxied) {
        var entity = proxied;
        if (entity != null && entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity)
                    .getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

}
