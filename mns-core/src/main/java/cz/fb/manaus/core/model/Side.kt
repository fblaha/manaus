package cz.fb.manaus.core.model

enum class Side {
    BACK {
        override val opposite: Side
            get() = LAY
    },
    LAY {
        override val opposite: Side
            get() = BACK
    };

    abstract val opposite: Side
}