package cz.fb.manaus.core.manager.filter

import cz.fb.manaus.core.model.MarketSnapshotEvent
import cz.fb.manaus.core.provider.ProviderSelector

interface MarketSnapshotEventFilter : ProviderSelector {

    fun accept(event: MarketSnapshotEvent): Boolean
}
