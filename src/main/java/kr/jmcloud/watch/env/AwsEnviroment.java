package kr.jmcloud.watch.env;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class AwsEnviroment {

	private static final ResourceBundle AWS_ENV_BUNDLE = ResourceBundle
			.getBundle("aws-env-conf");
	private static final String AWS_ALL_COMPUTES_INFO_FILE = AwsEnviroment
			.getAwsConf("AWS_ALL_COMPUTES_INFO_FILE");
	private static final String REGIONS = AwsEnviroment.getAwsConf("REGIONS");
	private static final String AWS_ENV_HOME_NAME = AwsEnviroment
			.getAwsConf("AWS_ENV_HOME_NAME");

	private AwsEnviroment() {
	}

	public static String getAwsConf(String confKey) {
		return getString(AWS_ENV_BUNDLE, confKey);
	}

	private static String getString(ResourceBundle rerourceBundle, String key) {
		try {
			return rerourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String[] getRegions() {
		return REGIONS.split(" ");
	}

	public static String getAWSEnvHome() {
		return AWS_ENV_HOME_NAME;
	}

	public static String getDefaultSaveFileName() {
		return AWS_ALL_COMPUTES_INFO_FILE;
	}

}
