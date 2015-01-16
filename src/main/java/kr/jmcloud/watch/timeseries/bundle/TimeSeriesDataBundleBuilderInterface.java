package kr.jmcloud.watch.timeseries.bundle;

import java.util.List;

import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public interface TimeSeriesDataBundleBuilderInterface {

	public enum BundleType {
		SAME_COUNT, SAME_TIME, METRIC_NAME, INSTANCE_ID, CUSTOM
	}

	public final int MINUTE = 1;
	public final int HOUR = 60 * MINUTE;
	public final int DAY = 24 * HOUR;
	public final int WEEK = 7 * DAY;

	public abstract TimeSeriesDOBundle buildBundleForSameMonitoringTime(
			String region, String instanceId, String metricName,
			String serviceType, long beforeTimeStamp, int monitoringMinutes);

	public abstract TimeSeriesDOBundle buildBundleForSameMonitoringCount(
			int monitoringCount, String region, String instanceId,
			String metricName, String serviceType, long beforeTimeStamp);

	public abstract TimeSeriesDOBundle buildBundle(String type, String region,
			String serviceType, List<TimeSeriesDO> timeSeriesDOList);

	public abstract TimeSeriesDOBundle buildBundleOfMetricNames(
			int intervalInMinutes, int counts, String region,
			String instanceId, String serviceType, long beforeTimeStamp,
			String... metricNames);

	public abstract TimeSeriesDOBundle buildBundleOfInstanceIds(
			int intervalInMinutes, int monitoringCount, String region,
			String metricName, String serviceType, long beforeTimeStamp,
			String... instanceIds);

	public abstract TimeSeriesDOBundle buildBundleOfAllInstanceIds(
			int intervalInMinutes, int monitoringCount, String region,
			String metricName, String serviceType, long beforeTimeStamp);

}