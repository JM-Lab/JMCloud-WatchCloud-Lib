package kr.jmcloud.watch.driver;

import kr.jmcloud.watch.compute.collector.driver.ComputesInfoCollectorDriverInterface;
import kr.jmcloud.watch.timeseries.collector.driver.TimeSeriesDataCollectorDriverInterface;

import org.apache.log4j.Logger;

public interface WatchCloudDriverInterface extends
		TimeSeriesDataCollectorDriverInterface,
		ComputesInfoCollectorDriverInterface {
	static Logger logger = Logger.getLogger(WatchCloudDriverInterface.class);
}
