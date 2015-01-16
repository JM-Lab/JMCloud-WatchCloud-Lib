package kr.jmcloud.watch.timeseries.converter;

import java.util.List;

import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public interface TimeSeriesDataConverterInterface<R> {

	public R convert(TimeSeriesDO tsDO);

	public R convert(TimeSeriesDOBundle tsDOB);

	public List<R> convertEachList(TimeSeriesDOBundle tsDOB);

}