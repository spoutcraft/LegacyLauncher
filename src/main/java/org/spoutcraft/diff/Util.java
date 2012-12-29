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
package org.spoutcraft.diff;

import java.io.IOException;
import java.io.InputStream;

/**
 * Java Binary patcher (based on bspatch by Colin Percival)
 *
 * @author Joe Desbonnet, joe@galway.net
 */
public class Util {
	/**
	 * Equiv of C library memcmp().
	 *
	 * @param s1
	 * @param s1offset
	 * @param s2
	 * @param n
	 * @return
	 */
	/*
	public final static int memcmp(byte[] s1, int s1offset, byte[] s2, int s2offset, int n) {
		if ((s1offset + n) > s1.length) {
			n = s1.length - s1offset;
		}
		if ((s2offset + n) > s2.length) {
			n = s2.length - s2offset;
		}
		for (int i = 0; i < n; i++) {
			if (s1[i + s1offset] != s2[i + s2offset]) {
				return s1[i + s1offset] < s2[i + s2offset] ? -1 : 1;
			}
		}

		return 0;
	}
	*/

	/**
	 * Equiv of C library memcmp().
	 *
	 * @param s1
	 * @param s1offset
	 * @param s2
	 * @param s2offset
	 * @return
	 */
	public final static int memcmp(byte[] s1, int s1offset, byte[] s2, int s2offset) {
		int n = s1.length - s1offset;

		if (n > (s2.length - s2offset)) {
			n = s2.length - s2offset;
		}
		for (int i = 0; i < n; i++) {
			if (s1[i + s1offset] != s2[i + s2offset]) {
				return s1[i + s1offset] < s2[i + s2offset] ? -1 : 1;
			}
		}

		return 0;
	}

	public static final boolean readFromStream(InputStream in, byte[] buf, int offset, int len) throws IOException {
		int totalBytesRead = 0;
		int nbytes;

		while (totalBytesRead < len) {
			nbytes = in.read(buf, offset + totalBytesRead, len - totalBytesRead);
			if (nbytes < 0) {
				System.err.println("readFromStream(): returning prematurely. Read "
						+ totalBytesRead + " bytes");
				return false;
			}
			totalBytesRead += nbytes;
		}

		return true;
	}
}
