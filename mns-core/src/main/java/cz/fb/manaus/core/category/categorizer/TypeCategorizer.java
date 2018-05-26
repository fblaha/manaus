package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
final public class TypeCategorizer extends AbstractDelegatingCategorizer {


    public static final String PREFIX = "type_";

    public TypeCategorizer() {
        super(PREFIX);
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        var type = market.getType();
        return Set.of(Optional.ofNullable(type).map(String::toLowerCase).orElse("unknown"));

    }
}
