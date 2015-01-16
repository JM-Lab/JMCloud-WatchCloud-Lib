package kr.jmcloud.watch.compute.collector.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.jm.commons.cli.RunCLI;
import kr.jm.commons.cli.RunCLILongTask;
import kr.jm.utils.JsonHelper;
import kr.jmcloud.watch.compute.data.ComputeVO;
import kr.jmcloud.watch.env.AwsEnviroment;

import com.google.gson.reflect.TypeToken;

public class AwsEc2InstancesInfoCollectorDriverWithCli implements
		ComputesInfoCollectorDriverInterface {

	private final String[] regions = AwsEnviroment.getAwsConf("REGIONS").split(
			" ");

	private final String service = "ec2";

	private final String startCommand = "/usr/local/bin/aws";

	@Override
	public Map<String, ComputeVO> collectComputesInfoInRegion(String region) {
		List<String> command = buildCLICommandOfDescribeInstances(region);
		List<Map<String, Object>> dataPoints = describeInstances(command);
		Map<String, ComputeVO> regionComputesInfo = new HashMap<String, ComputeVO>();
		for (Map<String, Object> dataPointInfo : dataPoints) {
			List<Map<String, Object>> instancesList = (List<Map<String, Object>>) dataPointInfo
					.get("Instances");
			for (Map<String, Object> instanceInfo : instancesList) {
				ComputeVO computeVO = buildComputeVO(instanceInfo);
				computeVO.setRegion(region);
				regionComputesInfo.put(computeVO.getComputeId(), computeVO);
			}

		}
		return regionComputesInfo;
	}

	private ComputeVO buildComputeVO(Map<String, Object> instanceInfo) {
		ComputeVO computeVO = new ComputeVO();
		computeVO.setComputeId((String) instanceInfo.get("InstanceId"));
		computeVO.setImageId((String) instanceInfo.get("ImageId"));
		computeVO.setComputeType((String) instanceInfo.get("InstanceType"));
		computeVO.setKeypairName((String) instanceInfo.get("KeyName"));
		computeVO.setLaunchTimestamp(fixLaunchTimestamp((String) instanceInfo
				.get("LaunchTime")));
		computeVO.setPrivateIp((String) instanceInfo.get("PrivateIpAddress"));
		computeVO.setPublicIp((String) instanceInfo.get("PublicIpAddress"));
		computeVO.setPrivateDns((String) instanceInfo.get("PrivateDnsName"));
		computeVO.setPublicDns((String) instanceInfo.get("PublicDnsName"));
		computeVO.setPlatform((String) instanceInfo.get("Hypervisor"));
		Map<String, Object> stateInfo = (Map<String, Object>) instanceInfo
				.get("State");
		computeVO.setState(extractStatus(stateInfo));
		List<Map<String, String>> securityGroupsList = (List<Map<String, String>>) instanceInfo
				.get("SecurityGroups");
		computeVO
				.setSecurityGroupList(extractSecurityGroupsList(securityGroupsList));
		computeVO.setGroupList(computeVO.getSecurityGroupList());
		List<Map<String, String>> tagsList = (List<Map<String, String>>) instanceInfo
				.get("Tags");
		computeVO.setComputeName(extractComputeName(tagsList));

		return computeVO;
	}

	private String fixLaunchTimestamp(String timestamp) {
		return timestamp.substring(0, timestamp.indexOf("."));
	}

	private List<String> extractSecurityGroupsList(
			List<Map<String, String>> securityGroupsList) {
		List<String> securityGroupList = new ArrayList<String>();
		for (Map<String, String> securityGroupInfo : securityGroupsList) {
			securityGroupList.add(securityGroupInfo.get("GroupName"));
		}
		return securityGroupList;
	}

	private String extractComputeName(List<Map<String, String>> tagsList) {
		for (Map<String, String> tagInfo : tagsList) {
			if ("Name".equals(tagInfo.get("Key"))
					&& tagInfo.get("Value") != null) {
				return tagInfo.get("Value");
			}
		}
		return "";
	}

	private String extractStatus(Map<String, Object> stateMap) {
		return (String) stateMap.get("Name");
	}

	private List<Map<String, Object>> describeInstances(List<String> command) {

		RunCLI cli = new RunCLILongTask();

		if (!runningCLI(cli, command)) {
			return new ArrayList<Map<String, Object>>();
		}

		return (List<Map<String, Object>>) JsonHelper.fromJsonString(
				cli.getResultOut(), new TypeToken<Map<String, Object>>() {
				}).get("Reservations");
	}

	private boolean runningCLI(RunCLI cli, List<String> command) {
		cli.run(command);
		return cli.statusAfterRunning();
	}

	private List<String> buildCLICommandOfDescribeInstances(String region) {
		List<String> command = new ArrayList<String>();
		command.add(startCommand);
		command.add("--region");
		command.add(region);
		command.add(service);
		command.add("describe-instances");
		return command;
	}

	@Override
	public String[] getRegions() {
		return regions;
	}

}
