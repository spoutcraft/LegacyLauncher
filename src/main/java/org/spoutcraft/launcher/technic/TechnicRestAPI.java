package org.spoutcraft.launcher.technic;

public class TechnicRestAPI {
	// Public
	public static final String REST_URL = "http://www.sctgaming.com/Technic/API/";
	public static final String MODPACKS_URL = REST_URL + "modpacks/";
	public static final String CACHE_URL = REST_URL + "cache/";
	public static final String MOD_URL = CACHE_URL + "mod/";

	public static String getModDownloadURL(String mod, String build) {
		return CACHE_URL + mod + "/" + build;
	}

	public static String getModMD5URL(String mod, String build) {
		return getModDownloadURL(mod, build) + "/MD5";
	}

	public static String getModpackInfoURL(String modpack, String build) {
		return MODPACKS_URL + modpack + "/build/" + build;
	}

	public static String getModpackBuildsURL(String modpack) {
		return MODPACKS_URL + modpack;
	}
}
