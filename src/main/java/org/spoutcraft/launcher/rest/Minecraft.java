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