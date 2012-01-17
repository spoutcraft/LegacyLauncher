/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
	 * @param n
	 * @return
	 */
	public final static int memcmp(byte[] s1, int s1offset, byte[] s2, int s2offset) {
		int n = s1.length - s1offset;

		if (n > (s2.length-s2offset)) {
			n = s2.length-s2offset;
		}
		for (int i = 0; i < n; i++) {
			if (s1[i + s1offset] != s2[i + s2offset]) {
				return s1[i + s1offset] < s2[i + s2offset] ? -1 : 1;
			}
		}

		return 0;
	}

	public static final boolean readFromStream (InputStream in, byte[] buf, int offset, int len) throws IOException {
		int totalBytesRead = 0;
		int nbytes;

		while ( totalBytesRead < len) {
			nbytes = in.read(buf,offset+totalBytesRead,len-totalBytesRead);
			if (nbytes < 0) {
				System.err.println ("readFromStream(): returning prematurely. Read "
						+ totalBytesRead + " bytes");
				return false;
			}
			totalBytesRead+=nbytes;
		}

		return true;
	}
}
