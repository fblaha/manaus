package cz.fb.manaus.reactor.categorizer;

import com.google.common.collect.RangeMap;

public interface CustomReciprocalRangeSupplier {

    RangeMap<Double, String> getCustomRanges();

}
