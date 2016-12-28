package cz.fb.manaus.core.category.categorizer;

public interface SimulationAware {

    default boolean isSimulationSupported() {
        return true;
    }

}
