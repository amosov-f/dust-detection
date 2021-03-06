package ru.spbu.astro.dust.algo.classify;

import org.jetbrains.annotations.NotNull;
import ru.spbu.astro.commons.Star;
import ru.spbu.astro.commons.StarFilter;
import ru.spbu.astro.commons.Stars;
import ru.spbu.astro.commons.spect.LuminosityClass;
import ru.spbu.astro.commons.spect.SpectType;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * User: amosov-f
 * Date: 03.05.15
 * Time: 1:23
 */
final class ExperimentalClassifier implements LuminosityClassifier {
    private static final Logger LOG = Logger.getLogger(ExperimentalClassifier.class.getName());


    private static final List<StarAttribute> STAR_ATTRIBUTES = Arrays.asList(
//            new StarAttribute("bv color", star -> star.getBVColor().val()),
//            new StarAttribute("mag", star -> star.getAbsMag().val()),
//            new StarAttribute("res", star -> star.getBVColor().val() - calculator.getSlope(star.getDir()).val() * star.getR().val()),
//            new StarAttribute("r", star -> star.getR().val()),
//            new StarAttribute("p", star -> star.getParallax().val()),
//            new StarAttribute("dr", star -> star.getR().err()),
//            new StarAttribute("vmag", Star::getVMag),
            new StarAttribute("pm", Star::getProperMotion)
    );

    @NotNull
    private final ArrayList<Attribute> attributes;

    @NotNull
    private final Classifier classifier;
    @NotNull
    private final Star[] stars;

    ExperimentalClassifier(@NotNull final Star[] stars) {
        this(stars, Mode.DEFAULT);
    }

    ExperimentalClassifier(@NotNull final Star[] stars, @NotNull final Mode mode) {
        attributes = new ArrayList<>();
        attributes.addAll(STAR_ATTRIBUTES.stream().map(StarAttribute::toAttribute).collect(Collectors.toList()));
        attributes.add(new Attribute(
                "luminosity classes",
                Arrays.stream(stars)
                        .map(Star::getSpectType)
                        .map(SpectType::getLumin)
                        .sorted()
                        .distinct()
                        .map(LuminosityClass::name)
                        .collect(Collectors.toList())
        ));

        final Instances dataset = toInstances("dataset", this.stars = stars);

        classifier = new SMO();
//        classifier.setFilterType(new SelectedTag(SMO.FILTER_NONE, SMO.TAGS_FILTER));
        try {
            classifier.buildClassifier(dataset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOG.info(classifier.toString());

        if (mode == Mode.DEFAULT) {
//            return;
        }

        try {
            final Evaluation evaluation = new Evaluation(dataset);
            evaluation.crossValidateModel(classifier, dataset, 10, new Random(0));

            LOG.info(evaluation.toSummaryString());
            LOG.info(evaluation.toMatrixString());
            LOG.info(evaluation.toClassDetailsString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    Star[] getStars() {
        return stars;
    }

//    double getA() {
//        return classifier.sparseWeights()[0][1][0];
//    }
//
//    double getB() {
//        return classifier.sparseWeights()[0][1][1];
//    }
//
//    double getC() {
//        return -classifier.bias()[0][1];
//    }

    @NotNull
    private Instances toInstances(@NotNull final String name, @NotNull final Star[] stars) {
        final Instances instances = new Instances(name, attributes, stars.length);
        instances.setClassIndex(attributes.size() - 1);
        instances.addAll(Arrays.stream(stars).map(star -> {
            final Instance instance = new DenseInstance(attributes.size());
            for (int i = 0; i < attributes.size() - 1; i++) {
                instance.setValue(attributes.get(i), STAR_ATTRIBUTES.get(i).apply(star));
            }
            instance.setValue(
                    attributes.get(attributes.size() - 1),
                    Objects.toString(star.getSpectType().getLumin())
            );
            return instance;
        }).collect(Collectors.toList()));
        return instances;
    }

    @NotNull
    @Override
    public LuminosityClass classify(@NotNull final Star star) {
        try {
            final int index = (int) classifier.classifyInstance(toInstances("predicted", new Star[]{star}).get(0));
            return LuminosityClass.MAIN[index];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum Mode {
        DEFAULT, TEST
    }

    private static class StarAttribute {
        @NotNull
        private final String name;
        @NotNull
        private final ToDoubleFunction<Star> f;

        StarAttribute(@NotNull final String name, @NotNull final ToDoubleFunction<Star> f) {
            this.name = name;
            this.f = f;
        }

        @NotNull
        Attribute toAttribute() {
            return new Attribute(name);
        }

        double apply(@NotNull final Star star) {
            return f.applyAsDouble(star);
        }
    }

    public static void main(String[] args) {
        final Star[] stars = StarFilter.of(Stars.ALL)
                .mainLumin()
//                .leftBV()
                .rightBV()
                .stars();
//        Arrays.sort(stars, Comparator.comparing(Star::getProperMotion));
//        int counter = 0;
//        int discounter = 0;
//        for (int i = 0; i < stars.length; i++) {
//            System.out.println(i);
//            for (int j = i + 1; j < stars.length; j++) {
//                boolean ok = stars[i].getProperMotion() < stars[j].getProperMotion();
//                ok &= stars[i].getSpectType().getLumin() == LuminosityClass.III && stars[j].getSpectType().getLumin() == LuminosityClass.V;
//                if (ok) {
//                    counter++;
//                } else {
//                    discounter++;
//                }
//            }
//        }
//        System.out.println(counter + " " + discounter);
       new ExperimentalClassifier(stars, Mode.TEST);
////        new ExperimentalClassifier(StarFilter.of(Stars.ALL).lumin(LuminosityClass.II, LuminosityClass.III, LuminosityClass.V).bv(0.3, 0.9).absMag(Double.NEGATIVE_INFINITY, 1).stars(), Mode.TEST);
    }
}
