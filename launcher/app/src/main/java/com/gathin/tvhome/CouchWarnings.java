package com.gathin.tvhome;

import java.util.Random;

/** Which power-on warning to show (res/values/couch_warnings.xml). Plain Java, so it is tested on the JVM. */
final class CouchWarnings {
    private CouchWarnings() {}

    /** pick's answer for the late-night warning. */
    static final int LATE = -1;
    /** The late-night warning shows from midnight up to this hour. */
    static final int LATE_UNTIL_HOUR = 5;

    /**
     * LATE between midnight and LATE_UNTIL_HOUR; otherwise one of count warnings at random, never last again
     * (last is the one shown before, or anything out of range when there was none).
     */
    static int pick(int count, int last, int hour, Random random) {
        if (hour >= 0 && hour < LATE_UNTIL_HOUR) return LATE;
        if (count <= 1) return 0;
        if (last < 0 || last >= count) return random.nextInt(count);
        int i = random.nextInt(count - 1);
        return i >= last ? i + 1 : i;
    }
}
