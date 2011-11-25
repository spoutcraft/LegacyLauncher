package org.spoutcraft.diff;
/*
 * Created on Feb 28, 2005
 */


import java.io.IOException;
import java.io.InputStream;

/**
 * @author @author Joe Desbonnet, joe@galway.net 
 *
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

	public static final boolean readFromStream (InputStream in, byte[] buf, int offset, int len)
	throws IOException 
	{
			
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
