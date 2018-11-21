package cz.fb.manaus.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class CollectedBets(val place: MutableList<Bet>,
                         val update: MutableList<Bet>,
                         val cancel: MutableList<String>) {

    val isEmpty: Boolean
        @JsonIgnore
        get() = place.isEmpty() && update.isEmpty() && cancel.isEmpty()

    companion object {
        fun create(): CollectedBets {
            return CollectedBets(mutableListOf(), mutableListOf(), mutableListOf())
        }
    }
}
