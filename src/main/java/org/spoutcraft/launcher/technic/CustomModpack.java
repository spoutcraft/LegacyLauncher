package org.spoutcraft.launcher.technic;

import org.codehaus.jackson.annotate.JsonProperty;

public class CustomModpack {
	@JsonProperty("name")
	private String displayName;

	@JsonProperty("user")
	private String user;

	@JsonProperty("friendly_name")
	private String name;

	@JsonProperty("version")
	private String version;

	@JsonProperty("url")
	private String url;

	@JsonProperty("logo")
	private String logoUrl;

	@JsonProperty("background")
	private String backgroundUrl;

	@JsonProperty("mirror")
	private boolean hasMirror;
	
	@JsonProperty("mirror_url")
	private String mirrorUrl;

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getVersion() {
		return version;
	}

	public String getUser() {
		return user;
	}

	public String getURL() {
		return url;
	}

	public String getLogoURL() {
		return logoUrl;
	}

	public String getBackgroundURL() {
		return backgroundUrl;
	}

	public boolean hasMirror() {
		return hasMirror;
	}

	public String getMirrorURL() {
		return mirrorUrl;
	}
}
