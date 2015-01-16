package kr.jmcloud.watch;

import java.io.File;
import java.util.List;
import java.util.Locale;

import kr.jm.utils.enums.OS;
import kr.jmcloud.watch.cloud.aws.AwsWatchCloudDriver;
import kr.jmcloud.watch.cloud.aws.WatchCloudResultFileServiceForAWS;
import kr.jmcloud.watch.driver.WatchCloudDriverInterface;
import kr.jmcloud.watch.timeseries.data.TimeSeriesDOBundle;

public class WatchCloudMain {

	public static void main(String[] args) {

		if (args.length < 5) {
			System.err
					.println("Required Arguments : IntervalInMinutes MonitoringCount Region MetricName ResultDirectory");
			System.exit(1);
		}

		int intervalInMinutes = parseInt(args[0]);
		int monitoringCount = parseInt(args[1]);
		String region = args[2];
		String metricName = args[3];
		String resultDirectory = args[4];

		WatchCloudDriverInterface watchCloudDriver = new AwsWatchCloudDriver();
		WatchCloudResultFileServiceForAWS csm = new WatchCloudResultFileServiceForAWS(
				watchCloudDriver, resultDirectory);
		if (!csm.updateAllComputesInfo()) {
			System.err.println("Can't Access Your Cloud!!!");
			System.exit(1);
		}

		long beforeTimestamp = System.currentTimeMillis();
		TimeSeriesDOBundle tsDOB = csm.buildTimeSeriesDOBundleOfAllInstances(
				intervalInMinutes, monitoringCount, region, metricName,
				beforeTimestamp);

		Locale defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
		List<File> fileList = csm.createChartImageWithNameList(tsDOB);
		fileList.add(csm.createChartImageWithName(tsDOB));
		Locale.setDefault(defaultLocale);


		fileList.addAll(csm.createJsonFileList(tsDOB));
		fileList.add(csm.createJsonFile(tsDOB));
		
		OS os = OS.getOs();
		for (File file : fileList) {
			os.open(file);
		}

	}

	private static int parseInt(String intString) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			System.err
					.println("Argument " + intString + " must be an integer.");
			System.exit(1);
		}
		return 0;
	}

}
