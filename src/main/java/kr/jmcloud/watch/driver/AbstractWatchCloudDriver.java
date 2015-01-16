package kr.jmcloud.watch.driver;

import java.util.Map;

import kr.jmcloud.watch.compute.collector.driver.ComputesInfoCollectorDriverInterface;
import kr.jmcloud.watch.compute.data.ComputeVO;
import kr.jmcloud.watch.timeseries.collector.driver.TimeSeriesDataCollectorDriverInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;

public class AbstractWatchCloudDriver implements WatchCloudDriverInterface {

	protected ComputesInfoCollectorDriverInterface cicDriver;

	protected TimeSeriesDataCollectorDriverInterface tsdcDriver;

	@Override
	public TimeSeriesDO collectTimeSeriseData(int period, String region,
			String metricName, String nameSpace, long startTimeStamp,
			long endTimeStamp, String instanceId) {
		return tsdcDriver.collectTimeSeriseData(period, region, metricName,
				nameSpace, startTimeStamp, endTimeStamp, instanceId);
	}

	@Override
	public Map<String, ComputeVO> collectComputesInfoInRegion(String region) {
		return cicDriver.collectComputesInfoInRegion(region);
	}

	@Override
	public String[] getRegions() {
		return cicDriver.getRegions();
	}

}