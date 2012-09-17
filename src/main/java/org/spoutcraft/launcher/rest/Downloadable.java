package org.spoutcraft.launcher.rest;

/**
 * Represents a downloadable file
 */
public interface Downloadable {
	/**
	 * Gets the full download url given the prefix to the maven repository
	 * 
	 * @param prefix
	 * @return full download url
	 */
	public String getUrl(String prefix);

	/**
	 * True if the md5 is a valid match
	 * 
	 * @param md5
	 * @return matches
	 */
	public boolean valid(String md5);
}
