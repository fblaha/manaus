package cz.fb.manaus.reactor.betting;

import com.google.common.base.CaseFormat;

public interface NameAware {

    default String getName() {
        var simpleName = this.getClass().getSimpleName();
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, simpleName);
    }

}
