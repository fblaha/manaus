package cz.fb.manaus.reactor.betting

import com.google.common.base.CaseFormat

interface NameAware {

    val name: String
        get() {
            // TODO no java
            val simpleName = this.javaClass.simpleName
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, simpleName)
        }

}
