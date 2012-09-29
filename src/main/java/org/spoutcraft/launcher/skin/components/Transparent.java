package org.spoutcraft.launcher.skin.components;

public interface Transparent {
	/**
	 * Gets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getTransparency();

	/**
	 * Sets the transparency of the label when no mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setTransparency(float t);

	/**
	 * Gets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values are between 0 and 1
	 * 
	 * @return transparency
	 */
	public float getHoverTransparency();

	/**
	 * Sets the transparency of the label when a mouse is hovering over it
	 * 
	 * Values should be between 0 and 1
	 * 
	 * @param t transparency
	 */
	public void setHoverTransparency(float t);
}
