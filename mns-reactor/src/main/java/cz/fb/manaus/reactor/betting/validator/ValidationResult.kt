package cz.fb.manaus.reactor.betting.validator

enum class ValidationResult {
    ACCEPT {
        override val isSuccess: Boolean = true
    },
    REJECT {
        override val isSuccess: Boolean = false
    };

    abstract val isSuccess: Boolean

    companion object {
        fun of(condition: Boolean): ValidationResult {
            return if (condition) {
                ACCEPT
            } else {
                REJECT
            }
        }
    }

}
