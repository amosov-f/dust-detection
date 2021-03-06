package ru.spbu.astro.dust.graph;

import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import static org.jfree.chart.JFreeChart.DEFAULT_TITLE_FONT;

@SuppressWarnings("MagicNumber")
public final class IdealExtPlot {
    @NotNull
    private final ChartFrame frame;

    public IdealExtPlot() {
        final XYItemRenderer renderer = new XYSplineRenderer();
        renderer.setSeriesShape(0, new Ellipse2D.Double(0, 0, 0, 0));
        renderer.setSeriesStroke(0, new BasicStroke(3));

        final XYPlot plot = new XYPlot(
                new XYSeriesCollection(getSeries()[0]),
                new NumberAxis("Расстояние"),
                new NumberAxis("Покраснение"),
                renderer
        );

        plot.getDomainAxis().setTickLabelsVisible(false);
        plot.getRangeAxis().setTickLabelsVisible(false);

//        plot.setDataset(1, new XYSeriesCollection(getSeries()[1]));
//        plot.setRenderer(1, new XYAreaRenderer());

        final JFreeChart chart = new JFreeChart("Покраснение в некотором направлении", DEFAULT_TITLE_FONT, plot, true);

        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
        chart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 16));

        frame = new ChartFrame("Идеальная кривая покраснения", chart);
        frame.pack();
    }

    public static void main(@NotNull final String[] args) {
        new IdealExtPlot().show();
    }

    @NotNull
    private XYSeries[] getSeries() {
        final XYSeries series = new XYSeries("Кривая покраснения");
        final XYSeries dustSeries = new XYSeries("Пылевые облака");

        double r = 0;
        double ext = 0;
        for (; r < 90; r += 1) {
            series.add(r, ext);
            ext += 0.00025;
        }

        ext += 10 * 0.00025;

        dustSeries.add(100, 0);
        dustSeries.add(100, ext + 0.002);

        ext += 10 * 0.00125;

        for (r = 110; r < 140; r += 1) {
            series.add(r, ext);
            dustSeries.add(r, ext);
            ext += 0.00125;
        }

        ext += 10 * 0.00125;

        dustSeries.add(150, ext - 0.002);
        dustSeries.add(150, 0);

        ext += 10 * 0.00025;

        for (r = 160; r < 240; r += 1) {
            series.add(r, ext);
            ext += 0.00025;
        }

        ext += 10 * 0.00025;

        dustSeries.add(250, 0);
        dustSeries.add(250, ext + 0.008);

        ext += 10 * 0.00325;

        for (r = 260; r < 265; r += 1) {
            series.add(r, ext);
            dustSeries.add(r, ext);
            ext += 0.00325;
        }

        ext += 10 * 0.00325;

        dustSeries.add(275, ext - 0.007);
        dustSeries.add(275, 0);

        ext += 10 * 0.00025;

        for (r = 285; r < 300; r += 1) {
            series.add(r, ext);
            ext += 0.00025;
        }


        ext += 10 * 0.00025;

        dustSeries.add(310, 0);
        dustSeries.add(310, ext + 0.005);

        ext += 10 * 0.00225;


        for (r = 320; r < 340; r += 1) {
            series.add(r, ext);
            dustSeries.add(r, ext);
            ext += 0.00225;
        }

        ext += 10 * 0.00225;

        dustSeries.add(350, ext - 0.005);
        dustSeries.add(350, 0);

        ext += 10 * 0.00025;

        for (r = 360; r < 400; r += 1) {
            series.add(r, ext);
            ext += 0.00025;
        }

        return new XYSeries[]{series, dustSeries};
    }

    private void show() {
        frame.setVisible(true);
    }
}
