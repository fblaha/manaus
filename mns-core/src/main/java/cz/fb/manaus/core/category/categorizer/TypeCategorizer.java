package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
final public class TypeCategorizer extends AbstractDelegatingCategorizer {


    public static final String PREFIX = "type_";

    public TypeCategorizer() {
        super(PREFIX);
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        String type = market.getType();
        if (type == null) {
            return Collections.singleton("unknown");
        }
        return singleton(type.toLowerCase());

    }
}
