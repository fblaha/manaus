package cz.fb.manaus.reactor.categorizer

import com.google.common.collect.RangeMap

interface CustomReciprocalRangeSupplier {

    val customRanges: RangeMap<Double, String>

}
