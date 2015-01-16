package kr.jmcloud.watch.compute;

import java.util.List;
import java.util.Map;

import kr.jmcloud.watch.compute.data.ComputeVO;

public interface AllComputesInfoInterface {

	public ComputeVO getComputeVO(String region, String instanceId);

	public Map<String, ComputeVO> getComputeVOInfo(String region);

	public Map<String, Map<String, ComputeVO>> getAllComputesInfo();

	public boolean updateAllComputesInfo();

	public boolean updateAllComputesInfo(String region);

	public List<String> getInstanceIdList(String region);

	public String getInstanceType(String region, String instanceId);

}