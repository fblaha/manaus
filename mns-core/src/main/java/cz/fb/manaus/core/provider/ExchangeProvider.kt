package cz.fb.manaus.core.provider

data class ExchangeProvider(val name: String,
                            val minAmount: Double,
                            val minPrice: Double,
                            val chargeRate: Double,
                            val isPerMarketCharge: Boolean)
