package cz.fb.manaus.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.math3.util.Precision;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Price {


    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private Side side;

    public Price(double price, double amount, Side side) {
        this.price = price;
        this.amount = amount;
        this.side = side;
    }

    public Price() {
    }

    public static double round(double val) {
        return Precision.round(val, 3);
    }

    public static boolean priceEq(double first, double second) {
        return Precision.equals(first, second, 0.0001d);
    }

    public static boolean amountEq(double first, double second) {
        return Precision.equals(first, second, 0.0001d);
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(price).append(amount).append(side).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Price other = (Price) obj;
        return new EqualsBuilder()
                .append(getAmount(), other.getAmount())
                .append(getPrice(), other.getPrice())
                .append(getSide(), other.getSide())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getAmount())
                .append(getPrice())
                .append(getSide())
                .toHashCode();
    }

}
