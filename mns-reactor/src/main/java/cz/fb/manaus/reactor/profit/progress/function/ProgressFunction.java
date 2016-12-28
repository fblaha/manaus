package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.betting.NameAware;
import org.apache.commons.lang3.StringUtils;

import java.util.OptionalDouble;

public interface ProgressFunction extends NameAware {

    @Override
    default String getName() {
        return StringUtils.removeEnd(NameAware.super.getName(), "Function");
    }

    OptionalDouble function(SettledBet bet);

}
