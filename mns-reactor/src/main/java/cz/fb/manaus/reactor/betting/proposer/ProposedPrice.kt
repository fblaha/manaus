package cz.fb.manaus.reactor.betting.proposer

data class ProposedPrice<T>(val price: T, val proposers: Set<String>)

