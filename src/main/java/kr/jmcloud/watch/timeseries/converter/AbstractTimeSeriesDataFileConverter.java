package kr.jmcloud.watch.timeseries.converter;

import java.io.File;

import kr.jm.utils.AutoStringBuilder;
import kr.jm.utils.FormatedTimeString;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public abstract class AbstractTimeSeriesDataFileConverter extends
		AbstractTimeSeriesDataConverter<File> {

	protected String buildeFilePathString(File dir, TimeSeriesDO tsDO,
			String title, String fileExtension) {
		return new AutoStringBuilder("_")
				.append(dir.getAbsolutePath(), File.separator,
						getTimeString(tsDO.getEndTimestamp()))
				.append(tsDO.getPeriod()).append(title.replaceAll("[ ]", "_"))
				.removeLastAutoAppendingString().getStringBuilder().append(".")
				.append(fileExtension).toString();
	}

	private String getTimeString(long timestamp) {
		return FormatedTimeString.getTime(timestamp,
				FormatedTimeString.SHORT_FORMAT_WITHOUT_TIMEZONE);
	}

	protected String getTitleForTSDO(TimeSeriesDO tsDO) {
		String title = new AutoStringBuilder(" ").append(tsDO.getRegion())
				.append(tsDO.getName()).append(tsDO.getMetricName())
				.autoToString();
		return title;
	}

	protected String getTitleForTSDOB(TimeSeriesDOBundle tsDOB) {
		String title = new AutoStringBuilder(" ").append(tsDOB.getRegion())
				.append("All").append("Instances")
				.append(tsDOB.getMetricName()).autoToString();
		return title;
	}

}