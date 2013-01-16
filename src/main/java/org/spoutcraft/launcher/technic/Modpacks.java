package org.spoutcraft.launcher.technic;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Modpacks {

	@JsonProperty("modpacks")
	private String[] modpacks;

	public List<String> getModpacks() {
		return Arrays.asList(modpacks);
	}

	@Override
	public String toString() {
		return "{ Modpacks [modpacks: " + modpacks + "] }";
	}
}
