package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.model.Market;

import java.util.Set;

public interface Categorizer extends SimulationAware, NamespaceAware {

    Set<String> getCategories(Market market);

}
