package org.spoutcraft.launcher;

import java.sql.Timestamp;

public class MiscUtils {
	
	public static Timestamp unixToTimestamp(long arg0) {
		Timestamp ts = new Timestamp(arg0);
		return ts;
	}
	
	public static long timestampToUnix(Timestamp arg0) {
		return arg0.getTime();
	}
}
