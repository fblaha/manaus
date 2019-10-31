package cz.fb.manaus.reactor.betting.validator

enum class ValidationResult {
    ACCEPT,
    REJECT,
    NOP;

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
