package kr.jmcloud.watch.timeseries.data;

import java.util.List;

import lombok.Data;

@Data
public class TimeSeriesDO {

	private String name;
	private String type;
	private String metricName;
	private String region;
	private String description;
	private String period; // ex)Hour, Day, Week ...
	private long startTimestamp;
	private long endTimestamp;
	private String unit;

	private List<Integer> sampleCount;
	private List<String> timeStamp;
	private List<Float> average;
	private List<Float> maximum;
	private List<Float> minimum;
	private List<Double> sum;

}