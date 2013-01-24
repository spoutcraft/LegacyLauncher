package org.spoutcraft.launcher.technic.skin;

public class PackButton extends ImageButton {
	private static final long serialVersionUID = 1L;
	private final int index;

	public PackButton(int index) {
		super();
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
