package org.spoutcraft.launcher.yml;

import org.spoutcraft.launcher.api.util.YAMLProcessor;

public interface YAMLResource {
	
	public YAMLProcessor getYAML();
	
	public boolean updateYAML();

}
