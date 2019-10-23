package cz.fb.manaus.reactor.betting.proposer

import cz.fb.manaus.core.provider.RequiredCapabilitiesAware
import cz.fb.manaus.reactor.betting.BetContext

interface DowngradeStrategy : RequiredCapabilitiesAware, (BetContext) -> Double
