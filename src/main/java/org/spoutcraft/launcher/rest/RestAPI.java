package org.spoutcraft.launcher.rest;

import org.spoutcraft.launcher.Channel;

public class RestAPI {
	public static final String REST_URL = "http://get.spout.org/nuget/";
	public static final String PROJECT = "spoutcraft";
	public static final String LIBRARIES = "libraries";
	public static final String VERSIONS_URL = REST_URL + "versions/" + PROJECT;
	public static final String INFO_URL = REST_URL + "info/";

	public static String getSpoutcraftURL(Channel channel) {
		return INFO_URL + channel.toString() + "/" + PROJECT;
	}

	public static String getLibraryURL(String build) {
		return INFO_URL + LIBRARIES + "/build/" + build;
	}
}
