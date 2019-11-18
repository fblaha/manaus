package cz.fb.manaus.reactor.filter

import cz.fb.manaus.core.model.BlacklistedCategory

interface BlacklistSupplier {

    fun getBlacklist(): List<BlacklistedCategory>

}