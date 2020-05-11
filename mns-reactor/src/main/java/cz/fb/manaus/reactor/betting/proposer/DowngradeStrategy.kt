package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.reactor.betting.BetEvent

typealias DowngradeStrategy = (BetEvent) -> Double?
