package kr.jmcloud.watch.timeseries.bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jm.utils.AutoStringBuilder;
import kr.jm.utils.enums.Period;
import kr.jmcloud.watch.compute.AllComputesInfoInterface;
import kr.jmcloud.watch.timeseries.collector.TimeSeriesDataCollectorInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public class TimeSeriesDataBundleBuilder implements
		TimeSeriesDataBundleBuilderInterface {

	protected TimeSeriesDataCollectorInterface collector;
	private AllComputesInfoInterface allComputesInfo;

	public TimeSeriesDataBundleBuilder(
			AllComputesInfoInterface allComputesInfo,
			TimeSeriesDataCollectorInterface collector) {
		this.allComputesInfo = allComputesInfo;
		this.collector = collector;
	}

	@Override
	public TimeSeriesDOBundle buildBundle(String type, String region,
			String serviceType, List<TimeSeriesDO> timeSeriesDOList) {
		TimeSeriesDOBundle tsDOB = new TimeSeriesDOBundle();

		Map<String, TimeSeriesDO> timeSeriesDOInfo = new HashMap<String, TimeSeriesDO>();
		for (TimeSeriesDO timeSeriesDO : timeSeriesDOList) {
			timeSeriesDOInfo.put(timeSeriesDO.getName(), timeSeriesDO);
		}

		tsDOB.setType(type);
		tsDOB.setRegion(region);
		tsDOB.setServiceType(serviceType);
		tsDOB.setTimeSeriesDOInfo(timeSeriesDOInfo);

		AutoStringBuilder autoSb = new AutoStringBuilder(", ");
		autoSb.append("{Type=", type).append("ServiceType=", serviceType)
				.append("BundleKeys=", timeSeriesDOInfo.keySet().toString())
				.removeLastAutoAppendingString();

		tsDOB.setDescription(autoSb.getStringBuilder().append("}").toString());
		return tsDOB;
	}

	private TimeSeriesDO selectCollectMethod(int intervalInMinutes,
			int monitoringCount, String region, String metricName,
			String serviceType, long beforeTimeStamp, String instanceId) {

		switch (intervalInMinutes) {
		case MINUTE:
			return collector.collectMinutely(monitoringCount, region,
					metricName, serviceType, beforeTimeStamp, instanceId);
		case HOUR:
			return collector.collectHourly(monitoringCount, region, metricName,
					serviceType, beforeTimeStamp, instanceId);
		case DAY:
			return collector.collectDaily(monitoringCount, region, metricName,
					serviceType, beforeTimeStamp, instanceId);
		case WEEK:
			return collector.collectWeekly(monitoringCount, region, metricName,
					serviceType, beforeTimeStamp, instanceId);
		default:
			return collector.collectEveryMinutes(intervalInMinutes,
					monitoringCount, region, metricName, serviceType,
					beforeTimeStamp, instanceId);
		}
	}

	@Override
	public TimeSeriesDOBundle buildBundleForSameMonitoringCount(
			int monitoringCount, String region, String instanceId,
			String metricName, String serviceType, long beforeTimeStamp) {

		TimeSeriesDO minuteTSDO = selectCollectMethod(MINUTE, monitoringCount,
				region, metricName, serviceType, beforeTimeStamp, instanceId);
		TimeSeriesDO hourTSDO = selectCollectMethod(HOUR, monitoringCount,
				region, metricName, serviceType, beforeTimeStamp, instanceId);
		TimeSeriesDO dayTSDO = selectCollectMethod(DAY, monitoringCount,
				region, metricName, serviceType, beforeTimeStamp, instanceId);
		TimeSeriesDO weekTSDO = selectCollectMethod(WEEK, monitoringCount,
				region, metricName, serviceType, beforeTimeStamp, instanceId);

		return buildBundle(BundleType.SAME_COUNT.name(), region, instanceId,
				serviceType, minuteTSDO, hourTSDO, dayTSDO, weekTSDO);
	}

	@Override
	public TimeSeriesDOBundle buildBundleForSameMonitoringTime(String region,
			String instanceId, String metricName, String serviceType,
			long beforeTimeStamp, int monitoringMinutes) {

		TimeSeriesDO minuteTSDO = selectCollectMethod(MINUTE,
				monitoringMinutes, region, metricName, serviceType,
				beforeTimeStamp, instanceId);
		TimeSeriesDO hourTSDO = selectCollectMethod(HOUR, monitoringMinutes
				/ HOUR, region, metricName, serviceType, beforeTimeStamp,
				instanceId);
		TimeSeriesDO dayTSDO = selectCollectMethod(DAY,
				monitoringMinutes / DAY, region, metricName, serviceType,
				beforeTimeStamp, instanceId);
		TimeSeriesDO weekTSDO = selectCollectMethod(WEEK, monitoringMinutes
				/ WEEK, region, metricName, serviceType, beforeTimeStamp,
				instanceId);

		return buildBundle(BundleType.SAME_TIME.name(), region, instanceId,
				serviceType, minuteTSDO, hourTSDO, dayTSDO, weekTSDO);
	}

	private TimeSeriesDOBundle buildBundle(String type, String region,
			String instanceId, String serviceType, TimeSeriesDO minuteTSDO,
			TimeSeriesDO hourTSDO, TimeSeriesDO dayTSDO, TimeSeriesDO weekTSDO) {
		minuteTSDO.setName(Period.MINUTELY.name());
		hourTSDO.setName(Period.HOURLY.name());
		dayTSDO.setName(Period.DAILY.name());
		weekTSDO.setName(Period.WEEKLY.name());

		List<TimeSeriesDO> timeSeriesDOList = new ArrayList<TimeSeriesDO>();
		timeSeriesDOList.add(minuteTSDO);
		timeSeriesDOList.add(hourTSDO);
		timeSeriesDOList.add(dayTSDO);
		timeSeriesDOList.add(weekTSDO);

		TimeSeriesDOBundle tsDOB = buildBundle(type, region, serviceType,
				timeSeriesDOList);

		return tsDOB;
	}

	@Override
	public TimeSeriesDOBundle buildBundleOfMetricNames(int monitoringMinutes,
			int monitoringCount, String region, String instanceId,
			String serviceType, long beforeTimeStamp, String... metricNames) {
		List<TimeSeriesDO> timeSeriesDOList = new ArrayList<TimeSeriesDO>();

		for (String metricName : metricNames) {
			timeSeriesDOList.add(selectCollectMethod(monitoringMinutes,
					monitoringCount, region, metricName, serviceType,
					beforeTimeStamp, instanceId));
		}

		return buildBundle(BundleType.METRIC_NAME.name(), region, serviceType,
				timeSeriesDOList);
	}

	@Override
	public TimeSeriesDOBundle buildBundleOfInstanceIds(int monitoringMinutes,
			int monitoringCount, String region, String metricName,
			String serviceType, long beforeTimeStamp, String... instanceIds) {
		List<TimeSeriesDO> timeSeriesDOList = new ArrayList<TimeSeriesDO>();

		for (String instanceId : instanceIds) {
			timeSeriesDOList.add(selectCollectMethod(monitoringMinutes,
					monitoringCount, region, metricName, serviceType,
					beforeTimeStamp, instanceId));
		}

		return buildBundle(BundleType.INSTANCE_ID.name(), region, serviceType,
				timeSeriesDOList);
	}

	@Override
	public TimeSeriesDOBundle buildBundleOfAllInstanceIds(
			int intervalInMinutes, int monitoringCount, String region,
			String metricName, String serviceType, long beforeTimeStamp) {
		List<String> instanceIdList = allComputesInfo.getInstanceIdList(region);
		String[] instanceIds = instanceIdList.toArray(new String[instanceIdList
				.size()]);
		return buildBundleOfInstanceIds(intervalInMinutes, monitoringCount,
				region, metricName, serviceType, beforeTimeStamp, instanceIds);
	}

}
