package cz.fb.manaus.core.category.categorizer

import java.util.regex.Pattern


fun compile(regex: String): Pattern {
    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
}

abstract class AbstractRegexpResolver(private val prefix: String) {

    protected fun getCategories(name: String?, patterns: Map<String, Pattern>): Set<String> {
        if (name == null) {
            return emptySet()
        }
        val result = mutableSetOf<String>()
        for ((k, pattern) in patterns.entries) {
            val matcher = pattern.matcher(name)
            if (matcher.matches()) {
                var key = k
                for (i in 1..matcher.groupCount()) {
                    key = key.replace("{$i}", matcher.group(i))
                }
                result.add(key)
            }
        }
        return result
    }

    protected fun addPrefix(key: String): String {
        return prefix + key
    }

}
