package org.spoutcraft.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Version {
	
	public File versionFile;
	public String version = null;
	
	public Version(File versionFile) {
		this.versionFile = versionFile;
	}
	
	public void read() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(versionFile));
		String line = null;
		
		if((line = br.readLine()) != null) {
			this.version = line;
		}
	}
	
	public boolean compare(String v) {
		if (this.version == null) return false;
		if (v == null) return false;
		String[] ver1 = this.version.split("\\.");
		String[] ver2 = v.split("\\."); 
		if (ver1.length != ver2.length) return false;
		
		for (int i = 0; i <= ver1.length - 1; i ++) {
			if (Integer.parseInt(ver1[i]) != Integer.parseInt(ver2[i])) return false;
		}
		return true;
	}
}
