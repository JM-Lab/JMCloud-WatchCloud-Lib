package kr.jmcloud.watch.compute.collector.driver;

import java.util.Map;

import kr.jmcloud.watch.compute.data.ComputeVO;

public interface ComputesInfoCollectorDriverInterface {

	public Map<String, ComputeVO> collectComputesInfoInRegion(String region);

	public String[] getRegions();

}
