package org.spoutcraft.launcher.rest;

import org.spoutcraft.launcher.Channel;

public class RestAPI {
	//Private
	private static final String PROJECT = "spoutcraft";

	//Public
	public static final String REST_URL = "http://get.spout.org/nuget/";
	public static final String VERSIONS_URL = REST_URL + "versions/" + PROJECT;
	public static final String INFO_URL = REST_URL + "info/";
	public static final String LIBRARY_GET_URL = REST_URL + "library/";

	public static String getSpoutcraftURL(Channel channel) {
		if (channel != Channel.CUSTOM) {
			return INFO_URL + channel.toString() + "/" + PROJECT;
		}
		throw new IllegalArgumentException("No download url available for custom channel builds");
	}

	public static String getSpoutcraftURL(String build) {
		return INFO_URL + "build/" + build + "/" + PROJECT;
	}

	public static String getDownloadURL(String build) {
		return REST_URL + "build/" + build + "/" + PROJECT + ".jar";
	}

	public static String getLibraryURL(String build) {
		return REST_URL + "libraries/build/" + build;
	}
	
	public static String getBuildListURL(Channel channel) {
		if (channel != Channel.CUSTOM) {
			return REST_URL + "builds/" + channel.toString() + "/" + PROJECT;
		}
		throw new IllegalArgumentException("No download url available for custom channel builds");
	}

	public static String getMD5URL(String md5) {
		return REST_URL + "hash/" + md5;
	}
}
