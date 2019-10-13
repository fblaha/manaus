package cz.fb.manaus.core.model

import cz.fb.manaus.core.provider.ExchangeProvider

data class Account(val provider: ExchangeProvider, val money: AccountMoney? = null)
