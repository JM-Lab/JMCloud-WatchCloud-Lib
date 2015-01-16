package kr.jmcloud.watch.compute.data;

import java.util.List;

import lombok.Data;

@Data
public class ComputeVO {

	private String computeName;
	private String computeId;
	private String imageId;
	private String computeType;
	private String keypairName;
	private String launchTimestamp;
	private String privateIp;
	private String publicIp;
	private String privateDns;
	private String publicDns;
	private String region;
	private String platform;
	private String state;
	private List<String> securityGroupList;
	private List<String> groupList;

}
