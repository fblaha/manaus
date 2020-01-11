package cz.fb.manaus.core

import com.google.common.base.CaseFormat

fun makeName(obj: Any): String {
    val simpleName = obj::class.simpleName ?: obj.javaClass.simpleName
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, simpleName)
}
