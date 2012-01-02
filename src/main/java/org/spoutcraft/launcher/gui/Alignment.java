package org.spoutcraft.launcher.gui;

public enum Alignment {
	
	NO_ALIGNMENT(0),
	LEADING(1),
	TRAILING(2),
	BASELINE(3),
	CENTER(4);
	
	private final int index;   

	Alignment(int index) {
        this.index = index;
    }

    public int index() { 
        return index; 
    }
}
