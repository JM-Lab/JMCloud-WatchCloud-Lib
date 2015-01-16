package kr.jmcloud.watch.env;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import kr.jm.utils.enums.OS;

public class SysEnviroment {

	private static final ResourceBundle SYS_ENV_BUNDLE = ResourceBundle
			.getBundle("sys-env-conf");

	private static final String HOME_NAME = SysEnviroment
			.getAwsConf("HOME_NAME");
	private static final String OUTPUT_NAME = SysEnviroment
			.getAwsConf("OUTPUT_NAME");
	private static final String SAVE_NAME = SysEnviroment
			.getAwsConf("SAVE_NAME");

	private SysEnviroment() {
	}

	public static String getAwsConf(String confKey) {
		return getString(SYS_ENV_BUNDLE, confKey);
	}

	private static String getString(ResourceBundle rerourceBundle, String key) {
		try {
			return rerourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getOsUserHome() {
		return OS.getUserHomeDir();
	}

	public static String getHomePath() {
		return OS.buildPath(getOsUserHome(), HOME_NAME);
	}

	public static String getPathInHome(String dirName) {
		return OS.buildPath(getOsUserHome(), HOME_NAME, dirName);
	}

	public static String getDefaultSavePath() {
		return getPathInHome(SAVE_NAME);
	}

	public static String getDefaultOutputPath() {
		return getPathInHome(OUTPUT_NAME);
	}

}
