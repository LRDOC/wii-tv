package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class CouchWarningsTest {

    @Test
    public void neverTheSameTwiceInARow() {
        Random random = new Random(3);
        int last = -1;
        for (int n = 0; n < 2000; n++) {
            int i = CouchWarnings.pick(6, last, 20, random);
            assertTrue(i >= 0 && i < 6);
            assertNotEquals(last, i);
            last = i;
        }
    }

    @Test
    public void everyWarningComesUp() {
        Random random = new Random(7);
        Set<Integer> seen = new HashSet<>();
        int last = -1;
        for (int n = 0; n < 200; n++) seen.add(last = CouchWarnings.pick(6, last, 12, random));
        assertEquals(6, seen.size());
    }

    @Test
    public void lateNightFromMidnightToFive() {
        Random random = new Random(1);
        for (int hour = 0; hour < 5; hour++) assertEquals(CouchWarnings.LATE, CouchWarnings.pick(6, 2, hour, random));
        for (int hour = 5; hour < 24; hour++) assertNotEquals(CouchWarnings.LATE, CouchWarnings.pick(6, 2, hour, random));
    }

    @Test
    public void firstStartAndSingleWarning() {
        Random random = new Random(5);
        int i = CouchWarnings.pick(6, -1, 15, random);
        assertTrue(i >= 0 && i < 6);
        assertEquals(0, CouchWarnings.pick(1, 0, 15, random));
        int stale = CouchWarnings.pick(3, 9, 15, random);
        assertTrue(stale >= 0 && stale < 3);
    }
}
