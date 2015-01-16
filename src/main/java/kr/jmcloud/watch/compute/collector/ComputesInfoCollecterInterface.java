package kr.jmcloud.watch.compute.collector;

import java.util.Map;

import kr.jmcloud.watch.compute.collector.driver.ComputesInfoCollectorDriverInterface;
import kr.jmcloud.watch.compute.data.ComputeVO;

public interface ComputesInfoCollecterInterface extends
		ComputesInfoCollectorDriverInterface {

	public Map<String, Map<String, ComputeVO>> collectAllComputesInfo();

}