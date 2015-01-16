package kr.jmcloud.watch.timeseries.collector.driver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kr.jm.commons.cli.RunCLI;
import kr.jm.commons.cli.RunCLILongTask;
import kr.jm.utils.FormatedTimeString;
import kr.jm.utils.JsonHelper;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;

public class CloudWatchTimeSeriesDataCollectorDriverWithCli implements
		TimeSeriesDataCollectorDriverInterface {

	private final String UNIT = "Unit";

	private final String SUM = "Sum";

	private final String MINIMUM = "Minimum";

	private final String MAXIMUM = "Maximum";

	private final String AVERAGE = "Average";

	private final String TIMESTAMP = "Timestamp";

	private final String SAMPLE_COUNT = "SampleCount";

	private final String service = "cloudwatch";

	private final String startCommand = "/usr/local/bin/aws";

	private final String getMetricStatics = "get-metric-statistics";

	private Comparator<Map<String, Object>> sorter = new Comparator<Map<String, Object>>() {
		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			return ((String) o1.get(TIMESTAMP)).compareTo(((String) o2
					.get(TIMESTAMP)));
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

		String startTime = changeTimestampString(startTimestamp);
		String endTime = changeTimestampString(endTimestamp);

		List<String> command = buildCLICommandOfGetMetricStatics(region,
				metricName, nameSpace, instanceId, startTime, endTime, period);

		TimeSeriesDO timeSeriesDO = buildTimeSeriesDO(getMetricStatics(command));
		timeSeriesDO.setMetricName(metricName);
		timeSeriesDO.setStartTimestamp(startTimestamp);
		timeSeriesDO.setEndTimestamp(endTimestamp);
		timeSeriesDO.setDescription(buildStringCommand(command));

		return timeSeriesDO;
	}

	private List<Map<String, Object>> getMetricStatics(List<String> command) {

		RunCLI cli = new RunCLILongTask();

		if (!runningCLI(cli, command)) {
			return new ArrayList<Map<String, Object>>();
		}

		List<Map<String, Object>> dataPoints = (List<Map<String, Object>>) JsonHelper
				.fromJsonString(cli.getResultOut(), Map.class)
				.get("Datapoints");
		Collections.sort(dataPoints, sorter);

		return dataPoints;
	}

	private boolean runningCLI(RunCLI cli, List<String> command) {
		cli.run(command);
		return cli.statusAfterRunning();
	}

	private TimeSeriesDO buildTimeSeriesDO(List<Map<String, Object>> dataPoints) {
		List<Integer> sampleCountList = new ArrayList<Integer>();
		List<String> timeStampList = new ArrayList<String>();
		List<Float> averageList = new ArrayList<Float>();
		List<Float> maximumList = new ArrayList<Float>();
		List<Float> minimumList = new ArrayList<Float>();
		List<Double> sumList = new ArrayList<Double>();

		for (Map<String, Object> dataPoint : dataPoints) {
			sampleCountList.add(((Double) dataPoint.get(SAMPLE_COUNT))
					.intValue());
			timeStampList.add(fixLaunchTimestamp((String) dataPoint
					.get(TIMESTAMP)));
			averageList.add(((Double) dataPoint.get(AVERAGE)).floatValue());
			maximumList.add(((Double) dataPoint.get(MAXIMUM)).floatValue());
			minimumList.add(((Double) dataPoint.get(MINIMUM)).floatValue());
			sumList.add((Double) dataPoint.get(SUM));
		}

		TimeSeriesDO timeSeriesDO = new TimeSeriesDO();
		timeSeriesDO.setSampleCount(sampleCountList);
		timeSeriesDO.setTimeStamp(timeStampList);
		timeSeriesDO.setAverage(averageList);
		timeSeriesDO.setMaximum(maximumList);
		timeSeriesDO.setMinimum(minimumList);
		timeSeriesDO.setSum(sumList);
		timeSeriesDO.setUnit(dataPoints.size() > 0 ? (String) dataPoints.get(0)
				.get(UNIT) : "");

		return timeSeriesDO;
	}

	private String fixLaunchTimestamp(String timestamp) {
		return timestamp.substring(0, timestamp.indexOf("Z"));
	}

	private List<String> buildCLICommandOfGetMetricStatics(String region,
			String metricName, String nameSpace, String instanceId,
			String startTime, String endTime, long period) {

		List<String> command = new ArrayList<String>();
		command.add(startCommand);
		command.add("--region");
		command.add(region);
		command.add(service);
		command.add(getMetricStatics);
		command.add("--metric-name");
		command.add(metricName);
		command.add("--start-time");
		command.add(startTime);
		command.add("--end-time");
		command.add(endTime);
		command.add("--period");
		command.add(new Long(period).toString());
		command.add("--namespace");
		command.add(nameSpace);
		command.add("--statistics");
		command.add("Sum");
		command.add("Maximum");
		command.add("Minimum");
		command.add("SampleCount");
		command.add("Average");
		command.add("--dimensions");
		command.add("Name=InstanceId,Value=" + instanceId);
		return command;
	}

	private String buildStringCommand(List<String> command) {
		if (command == null || command.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(command.get(0));
		for (int i = 1; i < command.size(); i++) {
			sb.append(' ');
			sb.append(command.get(i));
		}
		return sb.toString();
	}

}
