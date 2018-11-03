package cz.fb.manaus.core.category.categorizer

interface SimulationAware {

    val isSimulationSupported: Boolean
        get() = true

}
