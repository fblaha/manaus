package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.Price;

import java.util.Optional;

public interface PriceAdviser {

    Optional<Price> getNewPrice(BetContext betContext);

}
