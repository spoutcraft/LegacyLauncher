package org.spoutcraft.launcher.technic.rest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestObject {
	@JsonProperty("error")
	private String error;

	private RestAPI rest;

	public String getError() {
		return error;
	}

	public boolean hasError() {
		return error != null;
	}

	public void setRest(RestAPI rest) {
		this.rest = rest;
	}

	public RestAPI getRest() {
		return rest;
	}
}
