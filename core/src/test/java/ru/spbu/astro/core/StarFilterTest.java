package ru.spbu.astro.core;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StarFilterTest {
    @Test
    public void test() throws Exception {
        assertTrue(new StarFilter(Catalogues.HIPPARCOS_2007)
                .negativeExtinction()
                .filter(star -> star.getExtinction().getValue() >= 0).getStars().isEmpty());
    }
}