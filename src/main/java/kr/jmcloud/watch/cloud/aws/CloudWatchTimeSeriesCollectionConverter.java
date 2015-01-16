package kr.jmcloud.watch.cloud.aws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import kr.jm.utils.data.JMTime;
import kr.jmcloud.watch.timeseries.converter.AbstractTimeSeriesCollectionConverter;

public class CloudWatchTimeSeriesCollectionConverter extends
		AbstractTimeSeriesCollectionConverter {

	private Calendar calendar;

	public CloudWatchTimeSeriesCollectionConverter() {
		calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}

	@Override
	protected List<Date> changeTimestampStringToDateList(
			List<String> timeStampSeries) {
		List<Date> timestampList = new ArrayList<Date>();
		for (String time : timeStampSeries) {
			timestampList.add(extractTimestamp(time));
		}
		return timestampList;
	}

	private Date extractTimestamp(String time) {
		String[] timeArray = time.split("T");
		String[] ymdArray = timeArray[0].split("-");
		String[] hmsArray = timeArray[1].split(":");
		JMTime jmTime = new JMTime(ymdArray[0], ymdArray[1], ymdArray[2],
				hmsArray[0], hmsArray[1], hmsArray[2].substring(0, 2));
		// calendar - Month value is 0-based. e.g., 0 for January.
		calendar.set(jmTime.getYear(), jmTime.getMonth() - 1, jmTime.getDay(),
				jmTime.getHour(), jmTime.getMinute(), jmTime.getSecond());
		return calendar.getTime();
	}
}
