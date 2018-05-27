package cz.fb.manaus.manila.filter;

import com.google.common.collect.Range;
import cz.fb.manaus.reactor.price.AbstractPriceFilter;
import org.springframework.stereotype.Component;

@Component
public class AbnormalPriceFilter extends AbstractPriceFilter {

    public AbnormalPriceFilter() {
        super(3, 100, Range.all());
    }

}
