package cz.fb.manaus.reactor.betting.validator

enum class ValidationResult(val isSuccess: Boolean) {
    ACCEPT(true),
    REJECT(false),
    SKIP(false);

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
