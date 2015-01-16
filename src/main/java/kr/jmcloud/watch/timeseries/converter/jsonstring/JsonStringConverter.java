package kr.jmcloud.watch.timeseries.converter.jsonstring;

import kr.jm.utils.JsonHelper;
import kr.jm.utils.LogHelper;
import kr.jmcloud.watch.timeseries.converter.AbstractTimeSeriesDataConverter;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonStringConverter extends
		AbstractTimeSeriesDataConverter<String> {

	@Override
	public String convert(TimeSeriesDO tsDO) {
		LogHelper.logMethodStartInfo(log, "convert", tsDO);

		return JsonHelper.toJsonString(tsDO);
	}

	@Override
	public String convert(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "convert", tsDOB);

		return JsonHelper.toJsonString(tsDOB);
	}

}
