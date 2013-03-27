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
