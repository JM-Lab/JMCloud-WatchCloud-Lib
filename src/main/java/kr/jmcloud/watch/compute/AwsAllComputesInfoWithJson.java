package kr.jmcloud.watch.compute;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.jm.utils.JsonHelper;
import kr.jm.utils.enums.OS;
import kr.jmcloud.watch.compute.collector.ComputesInfoCollecterInterface;
import kr.jmcloud.watch.compute.data.ComputeVO;
import kr.jmcloud.watch.env.AwsEnviroment;
import kr.jmcloud.watch.env.SysEnviroment;

import com.google.gson.reflect.TypeToken;

public class AwsAllComputesInfoWithJson implements AllComputesInfoInterface {

	private File jsonFile = new File(OS.buildPath(
			SysEnviroment.getDefaultSavePath(), AwsEnviroment.getAWSEnvHome(),
			AwsEnviroment.getDefaultSaveFileName()));

	private Map<String, Map<String, ComputeVO>> allComputesInfo;

	private ComputesInfoCollecterInterface collector;

	public AwsAllComputesInfoWithJson(ComputesInfoCollecterInterface collector) {

		this.collector = collector;

		if (jsonFile.exists()) {
			allComputesInfo = JsonHelper.fromJsonFile(jsonFile,
					new TypeToken<Map<String, Map<String, ComputeVO>>>() {
					});
		} else {
			updateAllComputesInfo();
		}
	}

	public AwsAllComputesInfoWithJson(ComputesInfoCollecterInterface collector,
			File saveAsJsonFile) {
		this(collector);
		this.jsonFile = saveAsJsonFile;
	}

	@Override
	public ComputeVO getComputeVO(String region, String instanceId) {
		return getComputeVOInfo(region).get(instanceId);
	}

	@Override
	public Map<String, ComputeVO> getComputeVOInfo(String region) {
		return allComputesInfo.get(region);
	}

	@Override
	public Map<String, Map<String, ComputeVO>> getAllComputesInfo() {
		return allComputesInfo;
	}

	@Override
	public boolean updateAllComputesInfo() {
		allComputesInfo = collector.collectAllComputesInfo();
		if (jsonFile.exists()) {
			jsonFile.delete();
		}
		return JsonHelper.toJsonFile(allComputesInfo, jsonFile);
	}

	@Override
	synchronized public boolean updateAllComputesInfo(String region) {
		allComputesInfo.put(region,
				collector.collectComputesInfoInRegion(region));
		if (jsonFile.exists()) {
			jsonFile.delete();
		}
		return JsonHelper.toJsonFile(allComputesInfo, jsonFile);
	}

	@Override
	public List<String> getInstanceIdList(String region) {
		return new ArrayList<String>(getComputeVOInfo(region).keySet());
	}

	@Override
	public String getInstanceType(String region, String instanceId) {
		return getComputeVO(region, instanceId).getComputeType();
	}

}
