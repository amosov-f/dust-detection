package ru.spbu.astro.dust.algo;

import org.jetbrains.annotations.NotNull;
import ru.spbu.astro.dust.model.Catalogue;
import ru.spbu.astro.dust.model.SpectralType;
import ru.spbu.astro.dust.model.Star;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public final class LuminosityClassifier {
    private static final double RELATIVE_ERROR_LIMIT = 0.10;

    private static final List<SpectralType.LuminosityClass> LUMINOSITY_CLASSES
            = Arrays.asList(SpectralType.LuminosityClass.III, SpectralType.LuminosityClass.V);

    @NotNull
    private final Classifier classifier;

    public LuminosityClassifier(@NotNull final Catalogue catalogue) {
        this(catalogue, Mode.DEFAULT);
    }

    public LuminosityClassifier(@NotNull final Catalogue catalogue, @NotNull final Mode mode) {
        final List<Star> learnStars = new ArrayList<>();
        for (final Star star : catalogue.getStars()) {
            if (star.getParallax().getRelativeError() < RELATIVE_ERROR_LIMIT) {
                if (LUMINOSITY_CLASSES.contains(star.getSpectralType().getLuminosityClass())) {
                    learnStars.add(star);
                }
            }
        }
        final Instances learn = toInstances("learn", learnStars);

        classifier = new SMO();
        try {
            classifier.buildClassifier(learn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (mode == Mode.DEFAULT) {
            return;
        }

        try {
            final Evaluation evaluation = new Evaluation(learn);
            evaluation.crossValidateModel(classifier, learn, 10, new Random(0));

            System.out.println(evaluation.toSummaryString());
            System.out.println(evaluation.toMatrixString());
            System.out.println(evaluation.toClassDetailsString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public SpectralType.LuminosityClass getLuminosityClass(Star star) {
        //if (star.getSpectralType().getLuminosityClass() != null) {
        //    return star.getSpectralType().getLuminosityClass();
        //}

        try {
            int index = (int) classifier.classifyInstance(toInstances(star).get(0));
            return LUMINOSITY_CLASSES.get(index);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final ArrayList<Attribute> attributes = new ArrayList<Attribute>() {{
        add(new Attribute("bv color"));
        add(new Attribute("mag"));
        add(new Attribute(
                "luminosity classes",
                LUMINOSITY_CLASSES.stream().map(SpectralType.LuminosityClass::name).collect(Collectors.toList())
        ));
    }};

    @NotNull
    private static Instance toInstance(@NotNull final Star star) {
        return new DenseInstance(attributes.size()) {{
            setValue(attributes.get(0), star.getBVColor().getValue());
            setValue(attributes.get(1), star.getAbsoluteMagnitude().getValue());
            setValue(attributes.get(2), LUMINOSITY_CLASSES.indexOf(star.getSpectralType().getLuminosityClass()));
        }};
    }

    @NotNull
    private static Instances toInstances(@NotNull final String name, @NotNull final List<Star> stars) {
        final Instances instances = new Instances(name, attributes, stars.size());
        instances.setClassIndex(2);

        for (final Star s : stars) {
            instances.add(toInstance(s));
        }

        return instances;
    }

    @NotNull
    private static Instances toInstances(@NotNull final Star star) {
        final Instances instances = new Instances("predicted", attributes, 1);
        instances.setClassIndex(2);

        instances.add(toInstance(star));

        return instances;
    }

    public static enum Mode {
        DEFAULT, TEST
    }

    public static void main(@NotNull final String[] args) throws FileNotFoundException {
        new LuminosityClassifier(Catalogue.HIPPARCOS_2007, Mode.TEST);
    }
}