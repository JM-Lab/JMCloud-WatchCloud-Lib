package kr.jmcloud.watch.cloud.aws;

import kr.jmcloud.watch.compute.collector.driver.AwsEc2InstancesInfoCollectorDriver;
import kr.jmcloud.watch.driver.AbstractWatchCloudDriver;
import kr.jmcloud.watch.timeseries.collector.driver.CloudWatchTimeSeriesDataCollectorDriver;

import com.amazonaws.auth.AWSCredentials;

public class AwsWatchCloudDriver extends AbstractWatchCloudDriver {

	public AwsWatchCloudDriver() {
		cicDriver = new AwsEc2InstancesInfoCollectorDriver();
		tsdcDriver = new CloudWatchTimeSeriesDataCollectorDriver();
	}

	public AwsWatchCloudDriver(AWSCredentials credentials) {
		cicDriver = new AwsEc2InstancesInfoCollectorDriver(credentials);
		tsdcDriver = new CloudWatchTimeSeriesDataCollectorDriver(credentials);
	}
}
