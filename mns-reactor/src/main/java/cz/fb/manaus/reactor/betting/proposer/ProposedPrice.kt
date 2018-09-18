package cz.fb.manaus.reactor.betting.proposer

class ProposedPrice(val price: Double, val proposers: Set<String>) {
    constructor(price: Double, proposer: String) : this(price, setOf(proposer))
}
