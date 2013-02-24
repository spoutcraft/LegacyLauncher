/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spoutcraft.launcher.technic.rest;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Mod {
	private final String name;
	private final String version;
	private final String url;
	private final String md5;

	@JsonCreator
	public Mod(@JsonProperty("name") String name, @JsonProperty("version") String version, @JsonProperty("md5") String md5, @JsonProperty("url") String url) {
		this.name = name;
		this.version = version;
		this.md5 = md5;
		this.url = url;
	}

	public Mod(String name, String version, String url) {
		this(name, version, "", url);
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getURL() {
		return url;
	}

	public String getMD5() {
		return md5;
	}

	@Override
	public String toString() {
		return "{ Mod [name: " + name + ", version: " + version + "] }";
	}
}
