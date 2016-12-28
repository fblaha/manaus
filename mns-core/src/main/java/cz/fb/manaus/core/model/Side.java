package cz.fb.manaus.core.model;

public enum Side {
    BACK {
        @Override
        public Side getOpposite() {
            return LAY;
        }
    },
    LAY {
        @Override
        public Side getOpposite() {
            return BACK;
        }
    };

    public abstract Side getOpposite();

}
