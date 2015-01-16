package kr.jmcloud.watch.timeseries.collector;

import kr.jm.utils.enums.Seconds;
import kr.jmcloud.watch.timeseries.collector.driver.TimeSeriesDataCollectorDriverInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;

public interface TimeSeriesDataCollectorInterface extends
		TimeSeriesDataCollectorDriverInterface {

	public final int aSecond = Seconds.SECOND.getValue();
	public final int aMinute = Seconds.MINUTE.getValue();
	public final int anHour = Seconds.HOUR.getValue();
	public final int aDay = Seconds.DAY.getValue();
	public final int aWeek = Seconds.WEEK.getValue();

	public TimeSeriesDO collectEveryMinutes(int periodOfMinutes,
			int monitoringCount, String region, String metricName,
			String nameSpace, long beforeTimeStamp, String instanceId);

	public TimeSeriesDO collectMinutely(int monitoringMinutes, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId);

	public TimeSeriesDO collectHourly(int monitoringHours, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId);

	public TimeSeriesDO collectDaily(int monitoringDays, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId);

	public TimeSeriesDO collectWeekly(int monitoringWeeks, String region,
			String metricName, String nameSpace, long beforeTimeStamp,
			String instanceId);

}