package kr.jmcloud.watch.timeseries.collector;

import java.util.ArrayList;
import java.util.List;

import kr.jm.utils.LogHelper;
import kr.jm.utils.enums.Period;
import kr.jmcloud.watch.compute.AllComputesInfoInterface;
import kr.jmcloud.watch.timeseries.collector.driver.TimeSeriesDataCollectorDriverInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeSeriesDataCollector implements
		TimeSeriesDataCollectorInterface {

	private final String MINUTES = "MINUTES";
	private AllComputesInfoInterface allComputesInfo;
	private TimeSeriesDataCollectorDriverInterface driver;

	public TimeSeriesDataCollector(AllComputesInfoInterface allComputesInfo,
			TimeSeriesDataCollectorDriverInterface driver) {
		this.allComputesInfo = allComputesInfo;
		this.driver = driver;
	}

	private TimeSeriesDO collectData(int period, int monitoringCount,
			String region, String metricName, String nameSpace,
			long endTimeStamp, String name, String periodString,
			String instanceId) {

		long startTimeStamp = endTimeStamp - monitoringCount * period * 1000;

		TimeSeriesDO timeSeriesDO = collectTimeSeriseData(period, region,
				metricName, nameSpace, startTimeStamp, endTimeStamp, instanceId);
		timeSeriesDO.setName(name);
		timeSeriesDO.setRegion(region);
		timeSeriesDO.setType(allComputesInfo.getInstanceType(region, name));
		timeSeriesDO.setPeriod(periodString);
		return timeSeriesDO;
	}

	@Override
	public TimeSeriesDO collectEveryMinutes(int periodOfMinutes,
			int monitoringCount, String region, String metricName,
			String nameSpace, long beforeTimeStamp, String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectEveryMinutes");
		String name = periodOfMinutes + MINUTES;
		int period = periodOfMinutes * aMinute;
		return collectData(period, monitoringCount, region, metricName,
				nameSpace, beforeTimeStamp, instanceId, name, instanceId);
	}

	@Override
	public TimeSeriesDO collectMinutely(int monitoringMinutes, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectMinutely");
		return collectData(aMinute, monitoringMinutes, region, metricName,
				nameSpace, beforeTimeStamp, instanceId, Period.MINUTELY.name(),
				instanceId);
	}

	@Override
	public TimeSeriesDO collectHourly(int monitoringHours, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectHourly");
		return collectData(anHour, monitoringHours, region, metricName,
				nameSpace, beforeTimeStamp, instanceId, Period.HOURLY.name(),
				instanceId);
	}

	@Override
	public TimeSeriesDO collectDaily(int monitoringDays, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectDaily");
		return collectData(aDay, monitoringDays, region, metricName, nameSpace,
				beforeTimeStamp, instanceId, Period.DAILY.name(), instanceId);
	}

	@Override
	public TimeSeriesDO collectWeekly(int monitoringWeeks, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectWeekly");
		return collectData(aWeek, monitoringWeeks, region, metricName,
				nameSpace, beforeTimeStamp, instanceId, Period.WEEKLY.name(),
				instanceId);
	}

	@Override
	public TimeSeriesDO collectTimeSeriseData(int period, String region,
			String metricName, String nameSpace, long startTimeStamp,
			long endTimeStamp, String instanceId) {
		LogHelper.logMethodStartInfo(log, "collectAllComputesInfo");
		return driver.collectTimeSeriseData(period, region, metricName,
				nameSpace, startTimeStamp, endTimeStamp, instanceId);
	}

	public List<TimeSeriesDO> collectTimeSeriseData(String region,
			String metricName, String nameSpace, long startTimeStamp,
			long endTimeStamp, int period, String... instanceIds) {
		LogHelper.logMethodStartInfo(log, "collectTimeSeriseData");
		List<TimeSeriesDO> timeSeriesDOList = new ArrayList<TimeSeriesDO>();
		for (String instanceId : instanceIds) {
			TimeSeriesDO timeSeriesDO = driver.collectTimeSeriseData(period,
					region, metricName, nameSpace, startTimeStamp,
					endTimeStamp, instanceId);
			timeSeriesDOList.add(timeSeriesDO);
		}
		return timeSeriesDOList;
	}

	public List<TimeSeriesDO> collectTimeSeriseData(String region,
			String metricName, String nameSpace, long startTimeStamp,
			long endTimeStamp, int period) {
		LogHelper.logMethodStartInfo(log, "collectTimeSeriseData");
		List<String> instanceIdList = allComputesInfo.getInstanceIdList(region);
		String[] instanceIds = instanceIdList.toArray(new String[instanceIdList
				.size()]);
		return collectTimeSeriseData(region, metricName, nameSpace,
				startTimeStamp, endTimeStamp, period, instanceIds);
	}

}
