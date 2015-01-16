package kr.jmcloud.watch.timeseries.collector.driver;

import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;

public interface TimeSeriesDataCollectorDriverInterface {

	public TimeSeriesDO collectTimeSeriseData(int period, String region,
			String metricName, String nameSpace, long startTimestamp,
			long endTimestamp, String instanceId);

}