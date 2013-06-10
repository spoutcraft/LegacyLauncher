package org.spoutcraft.launcher.rest;

import org.codehaus.jackson.annotate.JsonProperty;

public class Minecraft extends RestObject {
	public static final String[] OLD_ASSETS = { "1.2.3", "1.2.5", "1.4.6", "1.4.7", "1.5" };
	public static final String PATCH_VERSION = "1.4.7";

	@JsonProperty("version")
	private String version;
	@JsonProperty("md5")
	private String md5;
	@JsonProperty("use_patch")
	private boolean usePatch;
	@JsonProperty("lwjgl")
	private String lwjgl;
	@JsonProperty("lwjgl_latest")
	private String lwjglLatest;

	public String getVersion() {
		return version;
	}

	public String getMd5() {
		return md5;
	}

	public boolean shouldUsePatch() {
		return usePatch;
	}

	public String getLwjgl() {
		return lwjgl;
	}

	public String getLwjglLatest() {
		return lwjglLatest;
	}

	@Override
	public String toString() {
		return "Minecraft: " + version + " md5: " + md5 + " use_patch: " + usePatch + " lwjgl: " + lwjgl + " lwjgl_latest " + lwjglLatest;
	}
}