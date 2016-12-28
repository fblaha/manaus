package cz.fb.manaus.reactor.betting.validator;

public enum ValidationResult {
    ACCEPT {
        @Override
        public boolean isSuccess() {
            return true;
        }
    }, REJECT {
        @Override
        public boolean isSuccess() {
            return false;
        }
    };

    public static ValidationResult of(boolean condition) {
        if (condition) {
            return ACCEPT;
        } else {
            return REJECT;
        }
    }

    public abstract boolean isSuccess();

}
