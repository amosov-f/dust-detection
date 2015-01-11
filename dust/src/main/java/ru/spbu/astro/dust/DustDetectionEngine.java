package ru.spbu.astro.dust;

import org.jetbrains.annotations.NotNull;
import ru.spbu.astro.dust.algo.DustTrendCalculator;
import ru.spbu.astro.core.func.HealpixDistribution;
import ru.spbu.astro.core.func.SphericDistribution;
import ru.spbu.astro.core.graph.HammerProjection;
import ru.spbu.astro.dust.graph.PixPlot;
import ru.spbu.astro.dust.model.Catalogues;
import ru.spbu.astro.dust.util.StarSelector;

import java.io.FileNotFoundException;

public final class DustDetectionEngine {
    public static void main(@NotNull final String[] args) throws FileNotFoundException {
        final DustTrendCalculator dustTrendCalculator = new DustTrendCalculator(
                new StarSelector(Catalogues.HIPPARCOS_UPDATED).parallaxRelativeError(0.25).getStars()
        );
        final SphericDistribution f = new HealpixDistribution(dustTrendCalculator.getSlopes());
        final HammerProjection hammerProjection = new HammerProjection(f);
        final PixPlot pixPlot = new PixPlot(dustTrendCalculator);
        hammerProjection.setProcessor(pixPlot::plot);
        hammerProjection.setVisible(true);
    }
}
