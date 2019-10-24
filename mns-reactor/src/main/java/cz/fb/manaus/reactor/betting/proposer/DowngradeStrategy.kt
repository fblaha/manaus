package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.provider.ProviderSelector
import cz.fb.manaus.reactor.betting.BetContext

interface DowngradeStrategy : ProviderSelector, (BetContext) -> Double
