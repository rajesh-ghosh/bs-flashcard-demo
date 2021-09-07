package org.bigspring.model;

import java.awt.event.ComponentListener;

public class AllEnums {

    public static enum CardType {
        TEXT,
        PICTURE;
    }

    public static enum Complexity {
        VERY_SIMPLE(1),
        SIMPLE(2),
        MODERATE(3),
        COMPLEX(4),
        VERY_COMPLEX(5);

        private final int ordinal;

        private Complexity(int ord) {
            this.ordinal = ord;
        }
    }
}
