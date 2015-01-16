package kr.jmcloud.watch.compute.collector.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.jm.utils.FormatedTimeString;
import kr.jmcloud.watch.compute.data.ComputeVO;
import kr.jmcloud.watch.env.AwsEnviroment;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class AwsEc2InstancesInfoCollectorDriver implements
		ComputesInfoCollectorDriverInterface {

	private final String[] regions = AwsEnviroment.getAwsConf("REGIONS").split(
			" ");

	private AmazonEC2Client amazonEC2Client;

	public AwsEc2InstancesInfoCollectorDriver() {
		amazonEC2Client = new AmazonEC2Client();
	}

	public AwsEc2InstancesInfoCollectorDriver(AWSCredentials credentials) {
		amazonEC2Client = new AmazonEC2Client(credentials);
	}

	private Set<Instance> describeInstances(String region) {
		amazonEC2Client.setRegion(RegionUtils.getRegion(region));

		List<Reservation> reservations = amazonEC2Client.describeInstances()
				.getReservations();
		Set<Instance> instances = new HashSet<Instance>();

		for (Reservation reservation : reservations) {
			instances.addAll(reservation.getInstances());
		}
		return instances;
	}

	private String changeTimestampString(long timestamp) {
		return FormatedTimeString.getTimeInUTC(timestamp,
				FormatedTimeString.LONG_FORMAT_WITHOUT_TIMEZONE);
	}

	private ComputeVO buildComputeVO(Instance instance) {
		ComputeVO computeVO = new ComputeVO();
		computeVO.setComputeId(instance.getInstanceId());
		computeVO.setImageId(instance.getImageId());
		computeVO.setComputeType(instance.getInstanceType());
		computeVO.setKeypairName(instance.getKeyName());
		computeVO.setLaunchTimestamp(changeTimestampString(instance
				.getLaunchTime().getTime()));
		computeVO.setPrivateIp(instance.getPrivateIpAddress());
		computeVO.setPublicIp(instance.getPublicIpAddress());
		computeVO.setPrivateDns(instance.getPrivateDnsName());
		computeVO.setPublicDns(instance.getPublicDnsName());
		computeVO.setPlatform(instance.getHypervisor());
		computeVO.setState(instance.getState().getName());
		computeVO.setSecurityGroupList(extractSecurityGroupsList(instance
				.getSecurityGroups()));
		computeVO.setGroupList(computeVO.getSecurityGroupList());
		computeVO.setComputeName(extractComputeName(instance.getTags()));

		return computeVO;
	}

	private List<String> extractSecurityGroupsList(
			List<GroupIdentifier> securityGroups) {
		List<String> securityGroupList = new ArrayList<String>();
		for (GroupIdentifier groupIdentifier : securityGroups) {
			securityGroupList.add(groupIdentifier.getGroupName());
		}
		return securityGroupList;
	}

	private String extractComputeName(List<Tag> tagsList) {
		for (Tag tag : tagsList) {
			if ("Name".equals(tag.getKey()) && tag.getValue() != null) {
				return tag.getValue();
			}
		}
		return "";
	}

	@Override
	public Map<String, ComputeVO> collectComputesInfoInRegion(String region) {
		Set<Instance> reservations = describeInstances(region);
		Map<String, ComputeVO> regionComputesInfo = new HashMap<String, ComputeVO>();
		for (Instance instance : reservations) {
			ComputeVO computeVO = buildComputeVO(instance);
			computeVO.setRegion(region);
			regionComputesInfo.put(computeVO.getComputeId(), computeVO);
		}
		return regionComputesInfo;
	}

	@Override
	public String[] getRegions() {
		return regions;
	}

}
