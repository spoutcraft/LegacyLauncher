package org.spoutcraft.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.bukkit.util.config.Configuration;
import org.spoutcraft.diff.JBPatch;
import org.spoutcraft.launcher.async.Download;
import org.spoutcraft.launcher.async.DownloadListener;

public class MinecraftDownloadUtils {
	private static boolean updated = false;
	private static File spoutcraftYML = new File(PlatformUtils.getWorkingDirectory(), "spoutcraft.yml");
	public static void downloadMinecraft(String user, String output, SpoutcraftBuild build, DownloadListener listener) throws IOException{
		int tries = 3;
		File outputFile = null;
		while (tries > 0) {
			System.out.println("Starting download of minecraft, with " + tries + " tries remaining");
			tries--;
			Download download = new Download(build.getMinecraftURL(user), output);
			download.setListener(listener);
			download.run();
			if (!download.isSuccess()) {
				if (download.getOutFile() != null) {
					download.getOutFile().delete();
				}
				System.err.println("Download of minecraft failed!");
				listener.stateChanged("Download Failed, retries remaining: " + tries, 0F);
			}
			else {
				String minecraftMD5 = MD5Utils.getMD5(FileType.minecraft, build.getLatestMinecraftVersion());
				String resultMD5 = MD5Utils.getMD5(download.getOutFile());
				System.out.println("Expected MD5: " + minecraftMD5 + " Result MD5: " + resultMD5);
				if (resultMD5.equals(minecraftMD5)) {
					if (!build.getLatestMinecraftVersion().equals(build.getMinecraftVersion())) {
						
						File patch = new File(PlatformUtils.getWorkingDirectory(), "mc.patch");
						Download patchDownload = DownloadUtils.downloadFile(build.getPatchURL(), patch.getPath(), null, null, listener);
						if (patchDownload.isSuccess()) {
							File patchedMinecraft = new File(GameUpdater.updateDir, "patched_minecraft.jar");
							patchedMinecraft.delete();
							JBPatch.bspatch(download.getOutFile(), patchedMinecraft, patch);
							String minecraft181MD5 = MD5Utils.getMD5(FileType.minecraft, build.getMinecraftVersion());
							resultMD5 = MD5Utils.getMD5(patchedMinecraft);
							
							if (minecraft181MD5.equals(resultMD5)) {
								outputFile = download.getOutFile();
								download.getOutFile().delete();
								GameUpdater.copy(patchedMinecraft, download.getOutFile());
								patchedMinecraft.delete();
								patch.delete();
								break;
							}
						}
					}
					else {
						outputFile = download.getOutFile();
					}
				}
			}
		}
		if (outputFile == null) {
			throw new IOException("Failed to download minecraft");
		}
		GameUpdater.copy(outputFile, new File(GameUpdater.binCacheDir, "minecraft_" + build.getMinecraftVersion() + ".jar"));
	}
	
	private static Configuration getSpoutcraftYML() {
		updateSpoutcraftYMLCache();
		Configuration config = new Configuration(spoutcraftYML);
		config.load();
		return config;
	}
	
	public static void updateSpoutcraftYMLCache() {
		if (!updated) {
			String urlName = MirrorUtils.getMirrorUrl("spoutcraft.yml", "http://mirror3.getspout.org/spoutcraft.yml", null);
			if (urlName != null) {
				try {
					URL url = new URL(urlName);
					HttpURLConnection con = (HttpURLConnection)(url.openConnection());
					System.setProperty("http.agent", "");
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					GameUpdater.copy(con.getInputStream(), new FileOutputStream(spoutcraftYML));
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			updated = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String[] getSpoutcraftBuilds() {
		Configuration config = getSpoutcraftYML();
		Map<Integer, String> builds = (Map<Integer, String>) config.getProperty("builds");
		int latest = config.getInt("latest", -1);
		int recommended = config.getInt("recommended", -1);
		
		if (builds != null) {
			String[] results = new String[builds.size()];
			int index = 0;
			for (Integer i : builds.keySet()) {
				results[index] = i.toString();
				if (i.intValue() == latest) {
					results[index] += "| Latest";
				}
				if (i.intValue() == recommended) {
					results[index] += "| Rec. Build";
				}
				index++;
			}
			return results;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static SpoutcraftBuild getSpoutcraftBuild() {
		Configuration config = getSpoutcraftYML();
		Map<Integer, String> builds = (Map<Integer, String>) config.getProperty("builds");
		int latest = config.getInt("latest", -1);
		int recommended = config.getInt("recommended", -1);
		int selected = SettingsUtil.getSelectedBuild();
		if (SettingsUtil.isRecommendedBuild()) {
			return new SpoutcraftBuild(builds.get(recommended), "1.0.0", recommended);
		}
		else if (SettingsUtil.isDevelopmentBuild()) {
			return new SpoutcraftBuild(builds.get(latest), "1.0.0", latest);
		}
		return new SpoutcraftBuild(builds.get(selected), "1.0.0", selected);
	}
	
	public static void getPatch() {
		
	}

}
