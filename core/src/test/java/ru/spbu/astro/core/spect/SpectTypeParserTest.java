package ru.spbu.astro.core.spect;

import org.junit.Test;
import ru.spbu.astro.util.Value;

import static org.junit.Assert.*;
import static ru.spbu.astro.core.spect.LuminosityClass.III;

public class SpectTypeParserTest {
    private static final double EPS = 1e-8;

    @Test
    public void testParsing1() {
        final SpectType type = SpectTypeParser.parse("K5III");
        assertNotNull(type);
        assertEquals(III, type.getLumin());
        final Value bv = type.toBV();
        assertNotNull(bv);
        assertEquals(1.450, bv.getValue(), EPS);
    }

    @Test
    public void testParsing2() {
        final SpectType type = SpectTypeParser.parse("Mb");
        assertNull(type);
    }

    @Test
    public void testParsing3() {
        final SpectType type = SpectTypeParser.parse("G8/K0III");
        assertNotNull(type);
        assertEquals(III, type.getLumin());
    }

    @Test
    public void testParsing4() throws Exception {
        final SpectType type = SpectTypeParser.parse("K2");
        assertNotNull(type);
        assertEquals(SpectClass.TypeSymbol.K, type.getSpect().getSymbol());
        assertEquals(2, type.getSpect().getNumber());
        assertNull(type.getLumin());
    }

    @Test
    public void testToString() {
        final SpectType type = SpectTypeParser.parse("B3/B4V");
        assertNotNull(type);
        assertEquals("B3/B4V", type.toString());
    }
}