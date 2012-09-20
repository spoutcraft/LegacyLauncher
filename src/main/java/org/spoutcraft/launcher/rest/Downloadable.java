package org.spoutcraft.launcher.rest;

import java.io.File;

import org.spoutcraft.launcher.exceptions.DownloadException;
import org.spoutcraft.launcher.util.DownloadListener;

/**
 * Represents a downloadable file
 */
public interface Downloadable {
	/**
	 * Downloads the file from the maven repository and saves it in the given location
	 * 
	 * @param file location to save the file
	 * @param listener download listener, optional
	 */
	public void download(File location, DownloadListener listener) throws DownloadException;

	/**
	 * True if the md5 is a valid match
	 * 
	 * @param md5
	 * @return matches
	 */
	public boolean valid(String md5);
	
	/**
	 * Name of the file
	 * 
	 * @return file name
	 */
	public String name();
}
