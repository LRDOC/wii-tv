package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class WiiThemeTest {

    @Test
    public void fifteenLightAndFiveDark() {
        int light = 0, dark = 0;
        Set<String> ids = new HashSet<>();
        for (WiiTheme t : WiiTheme.ALL) {
            assertTrue("duplicate id " + t.id, ids.add(t.id));
            if (t.dark) dark++;
            else light++;
        }
        assertEquals(15, light);
        assertEquals(5, dark);
        assertSame(WiiTheme.CLASSIC, WiiTheme.ALL.get(0));
    }

    @Test
    public void textReadsOnBackgroundAndPanel() {
        for (WiiTheme t : WiiTheme.ALL) {
            if (t.isClassic()) continue; // the console's own greys, kept as they were
            assertTrue(t.id + " text on background", WiiTheme.contrast(t.text, t.bg) >= 4.5);
            assertTrue(t.id + " text on panel", WiiTheme.contrast(t.text, t.panel) >= 4.5);
            assertTrue(t.id + " text on panel bottom", WiiTheme.contrast(t.text, t.panel2) >= 4.5);
            assertTrue(t.id + " label on plain tile", WiiTheme.contrast(t.label, t.plainBottom) >= 4.5);
        }
    }

    @Test
    public void unknownIdFallsBackToClassic() {
        assertSame(WiiTheme.CLASSIC, WiiTheme.byId("no-such-theme"));
        assertSame(WiiTheme.CLASSIC, WiiTheme.byId(null));
        assertEquals("blush", WiiTheme.byId("blush").id);
    }

    @Test
    public void mapPinsBothEnds() {
        assertEquals(0xFF102030, WiiTheme.map(10f, 10f, 0xFF102030, 20f, 0xFF203040));
        assertEquals(0xFF203040, WiiTheme.map(20f, 10f, 0xFF102030, 20f, 0xFF203040));
        assertEquals(0xFF000000, WiiTheme.map(-1000f, 10f, 0xFF102030, 20f, 0xFF203040));
    }
}
