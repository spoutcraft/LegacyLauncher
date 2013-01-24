package org.spoutcraft.launcher.technic.skin;

public class PackButton extends ImageButton {
	private static final long serialVersionUID = 1L;
	private int index;

	public PackButton() {
		super();
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
