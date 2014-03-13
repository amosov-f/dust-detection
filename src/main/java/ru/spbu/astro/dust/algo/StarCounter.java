package ru.spbu.astro.dust.algo;

import ru.spbu.astro.dust.model.Spheric;
import ru.spbu.astro.dust.model.Star;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class StarCounter {

    protected List<Star> stars = new ArrayList<>();
    protected double alpha;
    protected Double r;

    public StarCounter(final List<Star> stars, double alpha, Double r) {
        for (Star star : stars) {
            if (star.getR().value < 0) {
                continue;
            }
            if (r != null && star.getR().value > r) {
                continue;
            }
            this.stars.add(star);
        }
        this.alpha = alpha;
        this.r = r;
    }

    public List<Star> getConeStars(Spheric dir) {
        List<Star> result = new ArrayList<>();

        for (Star star : stars) {
            double mult = Math.sin(dir.b) * Math.sin(star.dir.b) +
                    Math.cos(dir.b) * Math.cos(star.dir.b) * Math.cos(star.dir.l - dir.l);

            if (Math.acos(mult) < alpha) {
                result.add(star);
            }
        }

        Collections.sort(result);

        return result;
    }
}
