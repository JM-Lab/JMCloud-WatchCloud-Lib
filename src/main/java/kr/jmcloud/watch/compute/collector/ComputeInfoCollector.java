package kr.jmcloud.watch.compute.collector;

import java.util.HashMap;
import java.util.Map;

import kr.jm.utils.LogHelper;
import kr.jmcloud.watch.compute.collector.driver.ComputesInfoCollectorDriverInterface;
import kr.jmcloud.watch.compute.data.ComputeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComputeInfoCollector implements ComputesInfoCollecterInterface {
	private ComputesInfoCollectorDriverInterface driver;

	public ComputeInfoCollector(ComputesInfoCollectorDriverInterface driver) {
		super();
		this.driver = driver;
	}

	@Override
	public Map<String, Map<String, ComputeVO>> collectAllComputesInfo() {
		LogHelper.logMethodStartInfo(log, "collectAllComputesInfo");
		Map<String, Map<String, ComputeVO>> allComputesInfo = new HashMap<String, Map<String, ComputeVO>>();
		for (String region : getRegions()) {
			allComputesInfo.put(region,
					driver.collectComputesInfoInRegion(region));
		}
		return allComputesInfo;
	}

	@Override
	public Map<String, ComputeVO> collectComputesInfoInRegion(String region) {
		LogHelper.logMethodStartInfo(log, "collectComputesInfoInRegion");
		return driver.collectComputesInfoInRegion(region);
	}

	@Override
	public String[] getRegions() {
		LogHelper.logMethodStartInfo(log, "getRegions");
		return driver.getRegions();
	}
}
