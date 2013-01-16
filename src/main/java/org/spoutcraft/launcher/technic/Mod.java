package org.spoutcraft.launcher.technic;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(using = ModDeserializer.class)
public class Mod {
	public String name;
	public String version;

	public Mod(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}
}
