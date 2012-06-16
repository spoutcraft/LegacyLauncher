package org.spoutcraft.launcher.yml;

import org.spoutcraft.launcher.api.util.YAMLProcessor;

public interface ResourceAction {
	
	public void beforeAction(YAMLProcessor previous);
	
	public void afterAction(YAMLProcessor current);

}
