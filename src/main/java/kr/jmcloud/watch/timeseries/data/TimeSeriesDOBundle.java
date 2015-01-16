package kr.jmcloud.watch.timeseries.data;

import java.util.Map;

import lombok.Data;

@Data
public class TimeSeriesDOBundle {

	private String region;
	private String metricName;
	private String serviceType;
	private String description;
	private String type;

	private Map<String, TimeSeriesDO> timeSeriesDOInfo;

}
