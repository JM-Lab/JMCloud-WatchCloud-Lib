package kr.jmcloud.watch.timeseries.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.jm.utils.LogHelper;
import kr.jm.utils.enums.Period;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;
import lombok.extern.slf4j.Slf4j;

import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;

@Slf4j
public abstract class AbstractTimeSeriesCollectionConverter extends
		AbstractTimeSeriesDataConverter<TimeSeriesCollection> {

	private final String KBYTES = "Kbytes";
	private final String BYTES = "Bytes";

	@Override
	public TimeSeriesCollection convert(TimeSeriesDO tsDO) {
		LogHelper.logMethodStartInfo(log, "convert", tsDO);
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		timeSeriesCollection.addSeries(extractAverage(tsDO));
		return timeSeriesCollection;
	}

	@Override
	public TimeSeriesCollection convert(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "convert", tsDOB);
		TimeSeriesCollection alltimeSeriesCollection = new TimeSeriesCollection();
		for (TimeSeriesDO tsDO : tsDOB.getTimeSeriesDOInfo().values()) {
			alltimeSeriesCollection.addSeries(extractAverage(tsDO));
		}
		return alltimeSeriesCollection;
	}

	private TimeSeries extractAverage(TimeSeriesDO tsDO) {
		List<Date> dateList = changeTimestampStringToDateList(tsDO
				.getTimeStamp());

		TimeSeries averageTS = new TimeSeries(tsDO.getName());

		List<Float> averageSeriesList = getAverageWithAutoUnitScale(tsDO);

		for (int i = 0; i < dateList.size(); i++) {
			averageTS.add(
					getRegulaTimePeriod(Period.valueOf(tsDO.getPeriod()),
							dateList.get(i)), averageSeriesList.get(i));
		}
		return averageTS;
	}

	private List<Float> getAverageWithAutoUnitScale(TimeSeriesDO tsDO) {
		if (BYTES.equals(tsDO.getUnit())) {
			List<Float> averageList = new ArrayList<Float>();
			for (Float changingValue : tsDO.getAverage()) {
				averageList.add(changingValue / 1024);
			}
			tsDO.setAverage(averageList);
			tsDO.setUnit(KBYTES);
		}

		return tsDO.getAverage();
	}

	private RegularTimePeriod getRegulaTimePeriod(Period period, Date date) {
		switch (period) {
		case DAILY:
			return new Day(date);
		case HOURLY:
			return new Hour(date);
		case MINUTELY:
			return new Minute(date);
		case WEEKLY:
			return new Week(date);
		default:
			return new FixedMillisecond(date);
		}
	}

	protected abstract List<Date> changeTimestampStringToDateList(
			List<String> timeStampSeries);

}