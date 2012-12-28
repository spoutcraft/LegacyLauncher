/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.util;

public enum FileType {
	JINPUT("a7835a73a130656aba23e34147a55367"),
	LWJGL("7a07c4285fa9a6b204ba59f011f1cd77"),
	LWJGL_UTIL("f00470751cfc093ba760ca3cf10a512c"),
	MINECRAFT("48677dc4c2b98c29918722b5ab27b4fd");

	private final String md5;
	private FileType(String md5) {
		this.md5 = md5;
	}

	public String getMD5() {
		return md5;
	}

	public String getMD5(String version) {
		if (this == MINECRAFT && "1.4.7".equals(version)) {
			return "48677dc4c2b98c29918722b5ab27b4fd";
		}
		if (this == MINECRAFT && "1.4.6".equals(version)) {
			return "48677dc4c2b98c29918722b5ab27b4fd";
		}
		if (this == MINECRAFT && "1.4.5".equals(version)) {
			return "b15e2b2b6b4629f0d99a95b6b44412a0";
		}
		if (this == MINECRAFT && "1.3.2".equals(version)) {
			return "969699f13e5bbe7f12e40ac4f32b7d9a";
		}
		if (this == MINECRAFT && "1.2.5".equals(version)) {
			return "8e8778078a175a33603a585257f28563";
		}
		return md5;
	}

	@Override
	public String toString() {
		return super.name().toLowerCase();
	}
}
