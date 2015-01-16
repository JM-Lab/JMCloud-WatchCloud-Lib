package kr.jmcloud.watch.timeseries.converter.jsonstring;

import java.io.File;

import kr.jm.utils.FileIO;
import kr.jm.utils.LogHelper;
import kr.jmcloud.watch.timeseries.converter.AbstractTimeSeriesDataFileConverter;
import kr.jmcloud.watch.timeseries.converter.TimeSeriesDataConverterInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonFileConverter extends AbstractTimeSeriesDataFileConverter {

	private TimeSeriesDataConverterInterface<String> jsonStringConverter = new JsonStringConverter();

	private File dir;

	private String jsonFileExtension = "json";

	public JsonFileConverter(File dir) {
		this.dir = dir;
	}

	@Override
	public File convert(TimeSeriesDO tsDO) {
		LogHelper.logMethodStartInfo(log, "convert", tsDO);

		String title = getTitleForTSDO(tsDO);

		File jsonFile = new File(buildeFilePathString(dir, tsDO, title,
				jsonFileExtension));
		String jsonString = jsonStringConverter.convert(tsDO);
		FileIO.writeString(jsonString, jsonFile);
		return jsonFile;
	}

	@Override
	public File convert(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "convert", tsDOB);

		String title = getTitleForTSDOB(tsDOB);

		TimeSeriesDO tsDO = tsDOB.getTimeSeriesDOInfo().values().iterator()
				.next();
		File jsonFile = new File(buildeFilePathString(dir, tsDO, title,
				jsonFileExtension));
		String jsonString = jsonStringConverter.convert(tsDOB);
		FileIO.writeString(jsonString, jsonFile);
		return jsonFile;
	}

}
