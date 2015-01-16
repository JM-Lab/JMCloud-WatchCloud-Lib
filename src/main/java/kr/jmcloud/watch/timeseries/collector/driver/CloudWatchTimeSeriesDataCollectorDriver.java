package kr.jmcloud.watch.timeseries.collector.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import kr.jm.utils.FormatedTimeString;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

public class CloudWatchTimeSeriesDataCollectorDriver implements
		TimeSeriesDataCollectorDriverInterface {

	private final String[] allStatistics = { "Sum", "Maximum", "Minimum",
			"SampleCount", "Average" };

	AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient();

	public CloudWatchTimeSeriesDataCollectorDriver() {
		cloudWatchClient = new AmazonCloudWatchClient();
	}

	public CloudWatchTimeSeriesDataCollectorDriver(AWSCredentials credentials) {
		cloudWatchClient = new AmazonCloudWatchClient(credentials);
	}

	private Comparator<Datapoint> sorter = new Comparator<Datapoint>() {
		@Override
		public int compare(Datapoint o1, Datapoint o2) {
			return o1.getTimestamp().compareTo(o2.getTimestamp());
		}
	};

	private String changeTimestampString(long timestamp) {
		return FormatedTimeString.getTimeInUTC(timestamp,
				FormatedTimeString.LONG_FORMAT_WITHOUT_TIMEZONE);
	}

	@Override
	public TimeSeriesDO collectTimeSeriseData(int period, String region,
			String metricName, String nameSpace, long startTimestamp,
			long endTimestamp, String instanceId) {

		cloudWatchClient.setRegion(RegionUtils.getRegion(region));

		Dimension instanceDimension = new Dimension();
		instanceDimension.setName("InstanceId");
		instanceDimension.setValue(instanceId);

		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withStartTime(new Date(startTimestamp))
				.withEndTime(new Date(endTimestamp)).withNamespace(nameSpace)
				.withPeriod(period).withMetricName(metricName)
				.withStatistics(allStatistics)
				.withDimensions(Arrays.asList(instanceDimension));

		GetMetricStatisticsResult metricStatisticsResult = cloudWatchClient
				.getMetricStatistics(request);

		List<Datapoint> dataPoints = metricStatisticsResult.getDatapoints();
		Collections.sort(dataPoints, sorter);

		TimeSeriesDO timeSeriesDO = buildTimeSeriesDO(dataPoints);
		timeSeriesDO.setMetricName(metricName);
		timeSeriesDO.setStartTimestamp(startTimestamp);
		timeSeriesDO.setEndTimestamp(endTimestamp);
		timeSeriesDO.setDescription(request.toString());

		return timeSeriesDO;
	}

	private TimeSeriesDO buildTimeSeriesDO(List<Datapoint> datapoints) {
		List<Integer> sampleCountList = new ArrayList<Integer>();
		List<String> timeStampList = new ArrayList<String>();
		List<Float> averageList = new ArrayList<Float>();
		List<Float> maximumList = new ArrayList<Float>();
		List<Float> minimumList = new ArrayList<Float>();
		List<Double> sumList = new ArrayList<Double>();

		for (Datapoint datapoint : datapoints) {
			sampleCountList.add(datapoint.getSampleCount().intValue());
			timeStampList.add(changeTimestampString(datapoint.getTimestamp()
					.getTime()));
			averageList.add(datapoint.getAverage().floatValue());
			maximumList.add(datapoint.getMaximum().floatValue());
			minimumList.add(datapoint.getMinimum().floatValue());
			sumList.add(datapoint.getSum());
		}

		TimeSeriesDO timeSeriesDO = new TimeSeriesDO();
		timeSeriesDO.setSampleCount(sampleCountList);
		timeSeriesDO.setTimeStamp(timeStampList);
		timeSeriesDO.setAverage(averageList);
		timeSeriesDO.setMaximum(maximumList);
		timeSeriesDO.setMinimum(minimumList);
		timeSeriesDO.setSum(sumList);
		timeSeriesDO.setUnit(datapoints.size() > 0 ? datapoints.get(0)
				.getUnit() : "");

		return timeSeriesDO;
	}

}
