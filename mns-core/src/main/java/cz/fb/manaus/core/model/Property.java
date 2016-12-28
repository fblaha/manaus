package cz.fb.manaus.core.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Date;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@NamedQueries({
        @NamedQuery(name = Property.DELETE_PREFIX, query = "delete from Property p where p.name like :prefix"),
        @NamedQuery(name = Property.DELETE_EXPIRED, query = "delete from Property p where p.expiryDate < :current"),
        @NamedQuery(name = Property.LIST_PROPERTIES, query = "select p from Property p where p.expiryDate >= :current and p.name like :prefix")
})
public class Property {
    public static final String DELETE_PREFIX = "PROP_DELETE_PREFIX";
    public static final String DELETE_EXPIRED = "PROP_DELETE_EXPIRED";
    public static final String LIST_PROPERTIES = "PROP_LIST_PROPERTIES";
    @Id
    private String name;
    @Column(nullable = false)

    private String value;
    @Column(nullable = false)
    private Date expiryDate;

    public Property() {
    }

    public Property(String name, String value, Date expiryDate) {
        this.name = name;
        this.value = value;
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
}
