/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the Spout License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {
	public int compare(String o1, String o2) {
		String[] versions = o1.split("\\.");
		String[] otherVersions = o2.split("\\.");

		int majorVersion = Integer.parseInt(versions[0]);
		int otherMajorVersion = Integer.parseInt(otherVersions[0]);

		int compare = compareVersions(otherMajorVersion, majorVersion);
		if (compare != 0) {
			return compare;
		}

		if (versions.length > 1 && otherVersions.length > 1) {
			int minorVersion = Integer.parseInt(versions[1]);
			int otherMinorVersion = Integer.parseInt(otherVersions[1]);

			compare = compareVersions(otherMinorVersion, minorVersion);
			if (compare != 0) {
				return compare;
			}

			if (versions.length > 2 && otherVersions.length > 2) {
				int buildVersion = Integer.parseInt(versions[2]);
				int otherBuildVersion = Integer.parseInt(otherVersions[2]);

				compare = compareVersions(otherBuildVersion, buildVersion);
				if (compare != 0) {
					return compare;
				}
				return 0;
			}
		}
		compare = compareVersions(otherVersions.length, versions.length);
		return compare;
	}

	private int compareVersions(int v1, int v2) {
		if (v1 > v2) {
			return 1;
		} else if (v1 < v2) {
			return -1;
		}
		return 0;
	}
}
