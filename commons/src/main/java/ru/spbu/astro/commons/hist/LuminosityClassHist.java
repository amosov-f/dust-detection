package ru.spbu.astro.commons.hist;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbu.astro.commons.Star;
import ru.spbu.astro.commons.spect.LuminosityClass;

/**
 * User: amosov-f
 * Date: 12.10.14
 * Time: 19:30
 */
public final class LuminosityClassHist extends StarHist<LuminosityClass, Integer> {
    public LuminosityClassHist() {
        super("Класс светимости");
    }

    @Nullable
    @Override
    protected LuminosityClass getX(@NotNull final Star star) {
        return star.getSpectType().getLumin();
    }

    @Nullable
    @Override
    protected Integer getY(@NotNull final Star[] stars) {
        return stars.length > 1 ? stars.length : null;
    }
}
