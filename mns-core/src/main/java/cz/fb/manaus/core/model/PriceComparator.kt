package cz.fb.manaus.core.model


object PriceComparator : Comparator<Price> {

    override fun compare(p1: Price, p2: Price): Int {
        // TODO clarify ordering
        return p2.compareTo(p1)
    }
}
