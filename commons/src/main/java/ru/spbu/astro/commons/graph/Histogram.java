package ru.spbu.astro.commons.graph;

import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Map;

/**
 * User: amosov-f
 * Date: 09.01.15
 * Time: 2:45
 */
public final class Histogram<T extends Comparable<T>> {
    @NotNull
    private final ChartFrame frame;

    public Histogram(@NotNull final Map<T, Double> hist,
                     @NotNull final String categoryLabel)
    {
        this(hist, categoryLabel, "Доля", true);
    }
    
    public Histogram(@NotNull final Map<T, Double> hist, 
                     @NotNull final String categoryLabel, 
                     @NotNull final String valueLabel,
                     final boolean percents) 
    {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        hist.forEach((type, value) -> dataset.addValue(value, "rowKey", type));

        final JFreeChart chart = ChartFactory.createBarChart(
                null,
                categoryLabel,
                valueLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        final StackedBarRenderer renderer = new StackedBarRenderer(false);
        renderer.setBaseItemLabelGenerator(new CategoryItemLabelGenerator() {
            @Nullable
            @Override
            public String generateRowLabel(@NotNull final CategoryDataset categoryDataset, final int i) {
                return null;
            }

            @Nullable
            @Override
            public String generateColumnLabel(@NotNull final CategoryDataset categoryDataset, final int i) {
                return null;
            }

            @Nullable
            @Override
            public String generateLabel(@NotNull final CategoryDataset categoryDataset, final int i, final int i2) {
                return percents ? null : String.format("%.2f", hist.get(Iterables.get(hist.keySet(), i2)));
            }
        });
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER));
        renderer.setBaseItemLabelsVisible(true);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(renderer);

        final NumberAxis rangeAxis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        if (percents) {
            rangeAxis.setNumberFormatOverride(new DecimalFormat("#%"));
        }
        rangeAxis.setRange(0.0, percents ? 1 : 1.1 * Collections.max(hist.values()));

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 16));

        frame = new ChartFrame(null, chart);
        frame.pack();
    }
    
    public void show() {
        frame.setVisible(true);
    }
}
