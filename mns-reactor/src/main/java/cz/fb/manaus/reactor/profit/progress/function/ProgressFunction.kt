package cz.fb.manaus.reactor.profit.progress.function

import com.google.common.base.CaseFormat
import cz.fb.manaus.core.model.SettledBet

interface ProgressFunction : (SettledBet) -> Double? {

    val name: String
        get() {
            val simpleName = this.javaClass.simpleName
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, simpleName).removeSuffix("Function")
        }

}
