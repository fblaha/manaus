package cz.fb.manaus.repository.domain


object PriceComparator : Comparator<Price> {

    override fun compare(p1: Price, p2: Price): Int {
        if (p1.side === p2.side) {
            return when (p1.side) {
                Side.BACK -> compareValuesBy(p2, p1, Price::price, Price::amount)
                Side.LAY -> compareValuesBy(p1, p2, Price::price, Price::amount)
            }
        }
        return compareValuesBy(p1, p2, Price::side)
    }
}
