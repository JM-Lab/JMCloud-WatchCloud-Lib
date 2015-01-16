package kr.jmcloud.watch.timeseries.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public abstract class AbstractTimeSeriesDataConverter<R> implements
		TimeSeriesDataConverterInterface<R> {

	@Override
	public List<R> convertEachList(TimeSeriesDOBundle tsDOB) {
		Map<String, TimeSeriesDO> tsDOMap = tsDOB.getTimeSeriesDOInfo();

		List<R> returnTypeList = new ArrayList<R>();

		for (TimeSeriesDO tsDO : tsDOMap.values()) {
			returnTypeList.add(convert(tsDO));
		}
		return returnTypeList;
	}

}
