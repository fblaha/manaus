package cz.fb.manaus.core.provider

data class ExchangeProvider(val name: String,
                            val minAmount: Double,
                            val minPrice: Double,
                            val chargeRate: Double, // TODO get rid of charge rate
                            val isPerMarketCharge: Boolean)
