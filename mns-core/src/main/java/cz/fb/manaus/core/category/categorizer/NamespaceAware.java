package cz.fb.manaus.core.category.categorizer;

import java.util.Optional;

public interface NamespaceAware {


    default boolean isGlobal() {
        return false;
    }

    default Optional<String> getNamespace() {
        return Optional.empty();
    }

}
