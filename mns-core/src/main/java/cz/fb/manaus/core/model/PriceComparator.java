package cz.fb.manaus.core.model;

import java.util.Comparator;

import static com.google.common.collect.ComparisonChain.start;

public class PriceComparator implements Comparator<Price> {

    public static final PriceComparator CMP = new PriceComparator();

    @Override
    public int compare(Price o1, Price o2) {
        if (o1.getSide() == o2.getSide()) {
            switch (o1.getSide()) {
                case BACK:
                    return start().compare(o2.getPrice(), o1.getPrice()).compare(o2.getAmount(), o1.getAmount()).result();
                case LAY:
                    return start().compare(o1.getPrice(), o2.getPrice()).compare(o1.getAmount(), o2.getAmount()).result();
                default:
                    throw new IllegalStateException();

            }
        }
        return start().compare(o1.getSide(), o2.getSide()).result();
    }

}
