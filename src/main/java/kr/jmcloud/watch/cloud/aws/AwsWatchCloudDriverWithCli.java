package kr.jmcloud.watch.cloud.aws;

import kr.jmcloud.watch.compute.collector.driver.AwsEc2InstancesInfoCollectorDriverWithCli;
import kr.jmcloud.watch.driver.AbstractWatchCloudDriver;
import kr.jmcloud.watch.timeseries.collector.driver.CloudWatchTimeSeriesDataCollectorDriverWithCli;

public class AwsWatchCloudDriverWithCli extends AbstractWatchCloudDriver {

	public AwsWatchCloudDriverWithCli() {
		cicDriver = new AwsEc2InstancesInfoCollectorDriverWithCli();
		tsdcDriver = new CloudWatchTimeSeriesDataCollectorDriverWithCli();
	}

}
