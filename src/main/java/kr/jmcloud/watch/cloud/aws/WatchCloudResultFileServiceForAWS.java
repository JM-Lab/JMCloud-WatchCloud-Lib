package kr.jmcloud.watch.cloud.aws;

import java.io.File;
import java.util.List;
import java.util.Map;

import kr.jm.utils.AutoStringBuilder;
import kr.jm.utils.LogHelper;
import kr.jmcloud.watch.compute.AllComputesInfoInterface;
import kr.jmcloud.watch.compute.AwsAllComputesInfoWithJson;
import kr.jmcloud.watch.compute.collector.ComputeInfoCollector;
import kr.jmcloud.watch.compute.data.ComputeVO;
import kr.jmcloud.watch.driver.WatchCloudDriverInterface;
import kr.jmcloud.watch.timeseries.bundle.TimeSeriesDataBundleBuilder;
import kr.jmcloud.watch.timeseries.bundle.TimeSeriesDataBundleBuilderInterface;
import kr.jmcloud.watch.timeseries.collector.TimeSeriesDataCollector;
import kr.jmcloud.watch.timeseries.converter.TimeSeriesDataConverterInterface;
import kr.jmcloud.watch.timeseries.converter.jfreechart.CloudWatchJFreeChartImageFileConverter;
import kr.jmcloud.watch.timeseries.converter.jsonstring.JsonFileConverter;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDO;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatchCloudResultFileServiceForAWS {

	private static final String AWS_EC2 = "AWS/EC2";

	private AllComputesInfoInterface allComputesInfo;

	private TimeSeriesDataBundleBuilderInterface tsdbBuilder;

	private TimeSeriesDataConverterInterface<File> chartConverter;

	private TimeSeriesDataConverterInterface<File> jsonFileConverter;

	public WatchCloudResultFileServiceForAWS(
			WatchCloudDriverInterface watchCloudDriver,
			String resultDirectoryPath) {
		this.allComputesInfo = new AwsAllComputesInfoWithJson(
				new ComputeInfoCollector(watchCloudDriver));
		this.tsdbBuilder = new TimeSeriesDataBundleBuilder(allComputesInfo,
				new TimeSeriesDataCollector(allComputesInfo, watchCloudDriver));
		this.chartConverter = new CloudWatchJFreeChartImageFileConverter(
				new File(resultDirectoryPath), 800, 400);
		this.jsonFileConverter = new JsonFileConverter(new File(
				resultDirectoryPath));
	}

	public WatchCloudResultFileServiceForAWS(
			WatchCloudDriverInterface watchCloudDriver,
			TimeSeriesDataConverterInterface<File> chartConverter,
			TimeSeriesDataConverterInterface<File> jsonFileConverter) {
		this.allComputesInfo = new AwsAllComputesInfoWithJson(
				new ComputeInfoCollector(watchCloudDriver));
		this.tsdbBuilder = new TimeSeriesDataBundleBuilder(allComputesInfo,
				new TimeSeriesDataCollector(allComputesInfo, watchCloudDriver));
		this.chartConverter = chartConverter;
		this.jsonFileConverter = jsonFileConverter;
	}

	public TimeSeriesDOBundle buildTimeSeriesDOBundleOfAllInstances(
			int timeKey, int monitoringCount, String region, String metricName,
			long beforeTimestamp) {
		LogHelper.logMethodStartInfo(log,
				"buildTimeSeriesDOBundleOfAllInstances", timeKey,
				monitoringCount, region, metricName, beforeTimestamp);
		TimeSeriesDOBundle tsDOB = tsdbBuilder.buildBundleOfAllInstanceIds(
				timeKey, monitoringCount, region, metricName, AWS_EC2,
				beforeTimestamp);
		tsDOB.setMetricName(metricName);
		return tsDOB;
	}

	private TimeSeriesDOBundle changeNameOfTimeSeriesDOs(
			TimeSeriesDOBundle tsDOB) {
		Map<String, ComputeVO> computeInfo = allComputesInfo
				.getComputeVOInfo(tsDOB.getRegion());
		Map<String, TimeSeriesDO> tsDOInfo = tsDOB.getTimeSeriesDOInfo();
		for (String instanceId : tsDOInfo.keySet()) {
			tsDOInfo.get(instanceId).setName(
					new AutoStringBuilder(" ")
							.append(instanceId)
							.append(computeInfo.get(instanceId)
									.getComputeName()).autoToString());
		}
		return tsDOB;
	}

	public boolean updateAllComputesInfo() {
		LogHelper.logMethodStartInfo(log, "updateAllComputesInfo");
		return allComputesInfo.updateAllComputesInfo();
	}

	public List<File> createChartImageWithNameList(TimeSeriesDOBundle tsDOB) {
		LogHelper
				.logMethodStartInfo(log, "createChartImageWithNameList", tsDOB);
		return chartConverter.convertEachList(changeNameOfTimeSeriesDOs(tsDOB));
	}

	public File createChartImageWithName(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "createChartImageWithName", tsDOB);
		return chartConverter.convert(changeNameOfTimeSeriesDOs(tsDOB));
	}

	public List<File> createJsonFileList(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "createJsonFileList", tsDOB);
		return jsonFileConverter.convertEachList(tsDOB);
	}

	public File createJsonFile(TimeSeriesDOBundle tsDOB) {
		LogHelper.logMethodStartInfo(log, "createJsonFile", tsDOB);
		return jsonFileConverter.convert(tsDOB);
	}

}
