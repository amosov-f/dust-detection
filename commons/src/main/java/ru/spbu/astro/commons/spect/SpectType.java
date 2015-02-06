package ru.spbu.astro.commons.spect;

import com.google.common.primitives.Doubles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbu.astro.util.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

public final class SpectType {
    @NotNull
    private final List<SpectClass> spects;
    @NotNull
    private final List<LuminosityClass> lumins;
    @NotNull
    private final Relation spectsRelation;
    @NotNull
    private final Relation luminsRelation;

    SpectType(@NotNull final List<SpectClass> spects,
              @NotNull final Relation spectsRelation,
              @NotNull final List<LuminosityClass> lumins,
              @NotNull final Relation luminsRelation) {
        this.spects = spects;
        this.spectsRelation = spectsRelation;
        this.lumins = lumins;
        this.luminsRelation = luminsRelation;
    }

    @NotNull
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < spects.size(); ++i) {
            s += spects.get(i);
            if (i < spects.size() - 1) {
                s += spectsRelation.getSymbol();
            }
        }
        for (int i = 0; i < lumins.size(); ++i) {
            s += lumins.get(i);
            if (i < lumins.size() - 1) {
                s += luminsRelation.getSymbol();
            }
        }
        return s;
    }

    @Nullable
    public Value toBV() {
        final List<Double> bvs = new ArrayList<>();
        for (final SpectClass spect : spects) {
            for (final LuminosityClass lumin : lumins) {
                final Double bv = SpectTable.getInstance().getBV(spect, lumin);
                if (bv != null) {
                    bvs.add(bv);
                }
            }
        }
        if (bvs.isEmpty()) {
            return null;
        }
        final double bv = DoubleStream.of(Doubles.toArray(bvs)).average().getAsDouble();
        return new Value(bv, bvs.stream().mapToDouble(x -> Math.abs(x - bv)).max().getAsDouble());
    }

    @Nullable
    public LuminosityClass getLumin() {
        if (!hasLumin()) {
            return null;
        }
        return lumins.get(0);
    }

    public boolean hasLumin() {
        return !lumins.isEmpty();
    }

    public SpectType setLumin(@NotNull final LuminosityClass lumin) {
        if (hasLumin()) {
            throw new RuntimeException("Already has luminosity class!");
        }
        return new SpectType(spects, spectsRelation, Collections.singletonList(lumin), luminsRelation);
    }

    @NotNull
    public SpectClass getSpect() {
        return SpectClass.valueOf(spects.stream().mapToInt(SpectClass::getCode).sum() / spects.size());
    }

    enum Relation {
        OR('/'), INTERMEDIATE('-');

        private final char symbol;

        Relation(final char symbol) {
            this.symbol = symbol;
        }

        @Nullable
        public static Relation parse(final char c) {
            for (final Relation relation : values()) {
                if (relation.getSymbol() == c) {
                    return relation;
                }
            }
            return null;
        }

        public char getSymbol() {
            return symbol;
        }
    }
}