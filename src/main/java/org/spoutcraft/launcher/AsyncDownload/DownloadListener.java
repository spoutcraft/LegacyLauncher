package org.spoutcraft.launcher.AsyncDownload;

/**
 * Listens for async file download state.
 */
public interface DownloadListener {
    public void stateChanged(String fileName, float progress);
}
