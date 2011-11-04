package org.spoutcraft.launcher;

import java.io.File;

public class ClassFile {
	private File file;
	String path;
	public ClassFile(File file, String rootDir) {
		this.file = file;
		path = file.getPath();
		path = path.replace(rootDir, "");
		path = path.replaceAll("\\\\", "/");
	}
	
	public ClassFile(String path) {
		this.file = null;
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ClassFile)) {
			return false;
		}
		return ((ClassFile)obj).path.equals(path);
	}
	
	public int hashCode(){
		return path.hashCode();
	}

}
