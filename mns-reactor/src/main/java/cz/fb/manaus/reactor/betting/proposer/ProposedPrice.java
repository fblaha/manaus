package cz.fb.manaus.reactor.betting.proposer;

import java.util.Collections;
import java.util.Set;

public class ProposedPrice {
    private final double price;
    private final Set<String> proposers;

    public ProposedPrice(double price, Set<String> proposers) {
        this.price = price;
        this.proposers = proposers;
    }

    public ProposedPrice(double price, String proposer) {
        this(price, Collections.singleton(proposer));
    }

    public double getPrice() {
        return price;
    }

    public Set<String> getProposers() {
        return proposers;
    }
}
