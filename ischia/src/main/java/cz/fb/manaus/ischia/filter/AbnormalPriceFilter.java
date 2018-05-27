package cz.fb.manaus.ischia.filter;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.price.AbstractPriceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AbnormalPriceFilter extends AbstractPriceFilter {

    @Autowired
    public AbnormalPriceFilter(@Qualifier("priceBulldoze") double priceBulldoze) {
        super(3, priceBulldoze, Range.closed(1.03d, 100d));
    }
}
