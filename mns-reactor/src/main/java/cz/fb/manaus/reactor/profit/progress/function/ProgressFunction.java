package cz.fb.manaus.reactor.profit.progress.function;

import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.reactor.betting.NameAware;
import org.apache.commons.lang3.StringUtils;

import java.util.OptionalDouble;
import java.util.function.Function;

public interface ProgressFunction extends Function<SettledBet, OptionalDouble>, NameAware {

    @Override
    default String getName() {
        return StringUtils.removeEnd(NameAware.super.getName(), "Function");
    }

}
