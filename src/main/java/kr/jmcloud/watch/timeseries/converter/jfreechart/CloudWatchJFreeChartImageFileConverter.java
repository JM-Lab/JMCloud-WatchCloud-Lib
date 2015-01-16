package kr.jmcloud.watch.timeseries.converter.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import kr.jm.utils.FormatedTimeString;
import kr.jm.utils.LogHelper;
import kr.jmcloud.watch.cloud.aws.CloudWatchTimeSeriesCollectionConverter;
import kr.jmcloud.watch.timeseries.converter.AbstractTimeSeriesDataFileConverter;
import kr.jmcloud.watch.timeseries.converter.TimeSeriesDataConverterInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;
import lombok.extern.slf4j.Slf4j;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

@Slf4j
public class CloudWatchJFreeChartImageFileConverter extends
		AbstractTimeSeriesDataFileConverter {

	private File dir;
	private int xSize;
	private int ySize;

	private TimeSeriesDataConverterInterface<TimeSeriesCollection> jfcDataConverter;

	private String chartImageFileExtension = "png";

	public CloudWatchJFreeChartImageFileConverter(File dir, int xSize, int ySize) {
		this.dir = dir;
		this.xSize = xSize;
		this.ySize = ySize;
		this.jfcDataConverter = new CloudWatchTimeSeriesCollectionConverter();
	}

	private File createChartImageFile(XYDataset dataset,
			File newChartImageFile, String title, String xLabel, String yLabel) {

		// Generate the graph
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, // Title
				xLabel, // x-axis Label
				yLabel, // y-axis Label
				dataset, // Dataset
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
				);

		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(10.0, 10.0, 10.0, 10.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		final XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setSeriesStroke(0, new BasicStroke(2.0f));
			renderer.setSeriesStroke(1, new BasicStroke(2.0f));
		}

		if (!newChartImageFile.getParentFile().exists()) {
			newChartImageFile.getParentFile().mkdirs();
		}

		try {
			ChartUtilities.saveChartAsPNG(newChartImageFile, chart, xSize,
					ySize);
		} catch (IOException e) {
			log.error("Problem occurred creating chart.", e);
			return newChartImageFile;
		}
		return newChartImageFile;

	}

	@Override
	public File convert(TimeSeriesDO tsDO) {
		LogHelper.logMethodStartInfo(log, "convert", tsDO);

		String title = getTitleForTSDO(tsDO);

		String xLabel = makeXLabelString(tsDO);
		String yLabel = tsDO.getUnit();
		File newImageFile = new File(buildeFilePathString(dir, tsDO, title,
				chartImageFileExtension));
		TimeSeriesCollection dataset = jfcDataConverter.convert(tsDO);
		return createChartImageFile(dataset, newImageFile, title, xLabel,
				yLabel);

	}

	@Override
	public File convert(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "convert", tsDOB);

		String title = getTitleForTSDOB(tsDOB);

		TimeSeriesDO tsDO = tsDOB.getTimeSeriesDOInfo().values().iterator()
				.next();
		String xLabel = makeXLabelString(tsDO);
		String yLabel = tsDO.getUnit();
		File newImageFile = new File(buildeFilePathString(dir, tsDO, title,
				chartImageFileExtension));
		return createChartImageFile(jfcDataConverter.convert(tsDOB),
				newImageFile, title, xLabel, yLabel);
	}

	private String makeXLabelString(TimeSeriesDO tsDO) {
		return tsDO.getPeriod() + "(" + getTimeString(tsDO.getEndTimestamp())
				+ ")";
	}

	private String getTimeString(long timestamp) {
		return FormatedTimeString.getTime(timestamp, "yyyy-MM-dd");
	}

}
