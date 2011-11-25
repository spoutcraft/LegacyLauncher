package org.spoutcraft.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class BackupCleanupThread extends Thread{
	File[] oldFiles;
	public BackupCleanupThread(File[] oldFiles) {
		this.oldFiles = oldFiles;
	}
	
	public void run() {
		ArrayList<Integer> builds = new ArrayList<Integer>();
		for (File file : oldFiles) {
			if (file.getPath().endsWith("-backup.zip")) {
				try {
					String path = file.getPath();
					String split[] = path.split("\\\\");
					path = split[split.length - 1];
					int build = Integer.parseInt(path.split("-")[0]);
					builds.add(build);
				}
				catch (Exception e) { }
			}
		}
		
		if (builds.size() < 6) {
			return;
		}
		
		Collections.sort(builds);
		
		int minSafeBuild = builds.get(builds.size() - 5);
		for (File file : oldFiles) {
			if (file.getPath().endsWith("-backup.zip")) {
				try {
					String path = file.getPath();
					String split[] = path.split("\\\\");
					path = split[split.length - 1];
					int build = Integer.parseInt(path.split("-")[0]);
					if (build < minSafeBuild) {
						file.delete();
					}
				}
				catch (Exception e) { }
			}
			else if (file.getPath().endsWith(".tmp")) {
				file.delete();
			}
		}
	}

}
