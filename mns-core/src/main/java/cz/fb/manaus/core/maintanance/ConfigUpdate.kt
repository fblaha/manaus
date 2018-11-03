package cz.fb.manaus.core.maintanance

import java.time.Duration

class ConfigUpdate private constructor(val deletePrefixes: MutableSet<String>,
                                       val setProperties: MutableMap<String, String>,
                                       val ttl: String) {
    companion object {
        val NOP = ConfigUpdate(mutableSetOf(), mutableMapOf(), "0m")

        fun empty(ttl: Duration): ConfigUpdate {
            return ConfigUpdate(mutableSetOf(), mutableMapOf(), ttl.toMinutes().toString() + "m")
        }
    }
}
