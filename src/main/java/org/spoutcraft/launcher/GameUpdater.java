/*
 * This file is part of Spoutcraft Launcher (http://wiki.getspout.org/).
 * 
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.spoutcraft.launcher.async.Download;
import org.spoutcraft.launcher.async.DownloadListener;
import org.spoutcraft.launcher.exception.UnsupportedOSException;
import org.spoutcraft.launcher.logs.SystemConsoleListener;

import SevenZip.LzmaAlone;

public class GameUpdater implements DownloadListener {
	/* Minecraft Updating Arguments */
	public long latestVersion;
	public String user = "Player";
	public String downloadTicket = "1";

	/* General Updating Settings */
	public boolean devmode = false;

	/* Files */
	public static final File binDir = new File(PlatformUtils.getWorkingDirectory().getPath() + File.separator + "bin");
	public static final File updateDir = new File(PlatformUtils.getWorkingDirectory().getPath() + File.separator + "temp");
	public static final File backupDir = new File(PlatformUtils.getWorkingDirectory().getPath() + File.separator + "backups");
	public static final File spoutcraftDir = new File(PlatformUtils.getWorkingDirectory().getPath() + File.separator + "spoutcraft");
	public static final File savesDir = new File(PlatformUtils.getWorkingDirectory().getPath() + File.separator + "saves");

	/* Minecraft Updating Arguments */
	public final String baseURL = "http://s3.amazonaws.com/MinecraftDownload/";
	public final String latestLWJGLURL = "http://www.minedev.net/spout/lwjgl/";
	//public final String spoutcraftDownloadURL = "http://ci.getspout.org/view/SpoutDev/job/Spoutcraft/promotion/latest/Recommended/artifact/target/spoutcraft-dev-SNAPSHOT.zip";
	//public final String spoutcraftDownloadDevURL = "http://ci.getspout.org/job/Spoutcraft/lastSuccessfulBuild/artifact/target/spoutcraft-dev-SNAPSHOT.zip";
	public final String spoutcraftMirrors = "http://cdn.getspout.org/mirrors.html";
	private SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));
	private DownloadListener listener;
	private byte allowUpdates = -1;
	private ArrayList<String> mirrors = new ArrayList<String>();
	private final Random rand = new Random();

	public GameUpdater() {
	}

	public void updateMC() throws Exception {
		purgeDir(binDir);
		purgeDir(updateDir);

		binDir.mkdir();
		updateDir.mkdir();

		// Processs minecraft.jar \\
		downloadFile(baseURL + "minecraft.jar?user=" + user + "&ticket=" + downloadTicket, GameUpdater.updateDir + File.separator + "minecraft.jar");

		File nativesDir = new File(binDir.getPath() + File.separator + "natives");
		nativesDir.mkdir();

		// Process other Downloads
		downloadFile(getNativesUrl() + "jinput.jar", GameUpdater.binDir.getPath() + File.separator + "jinput.jar");
		downloadFile(getNativesUrl() + "lwjgl.jar", GameUpdater.binDir.getPath() + File.separator + "lwjgl.jar");
		downloadFile(getNativesUrl() + "lwjgl_util.jar", GameUpdater.binDir.getPath() + File.separator + "lwjgl_util.jar");
		getNatives();

		// Extract Natives \\
		extractNatives(nativesDir, new File(GameUpdater.updateDir.getPath() + File.separator + "natives.zip"));

		writeVersionFile(new File(GameUpdater.binDir + File.separator + "version"), Long.toString(this.latestVersion));
	}
	
	public String getBuildUrl(String mirrorURI, String jenkinsURL) {
		if (mirrors.size() == 0) {
			try {
				updateMirrors();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		int random = rand.nextInt(10 * mirrors.size());
		int index = random / 10;
		//Test for bad, down mirrors
		for (int i = index; i < mirrors.size() + index; i++) {
			int j = i;
			if (j > mirrors.size()) j-= mirrors.size();
			String mirror = "http://" + mirrors.get(index) + "/" + mirrorURI;
			if (isAddressReachable(mirror)) {
				return mirror;
			}
		}
		System.err.println("All mirrors failed, defaulting to jenkins");
		return jenkinsURL;
	}
	
	public String getRecommendedBuildUrl() {
		return getBuildUrl("Spoutcraft/recommended/Spoutcraft.zip", "http://ci.getspout.org/view/SpoutDev/job/Spoutcraft/promotion/latest/Recommended/artifact/target/spoutcraft-dev-SNAPSHOT.zip");
	}
	
	public String getDevelopmentBuildUrl() {
		return getBuildUrl("Spoutcraft/latest/spoutcraft-dev-SNAPSHOT.zip", "http://ci.getspout.org/job/Spoutcraft/lastSuccessfulBuild/artifact/target/spoutcraft-dev-SNAPSHOT.zip");
	}
	
	public boolean isAddressReachable(String url) {
		try {
			URL test = new URL(url);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection urlConnect = (HttpURLConnection)test.openConnection();
			urlConnect.setRequestMethod("HEAD");
			return (urlConnect.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		}
	}
	
	public void updateMirrors() throws IOException {
		URL url = new URL("http://cdn.getspout.org/mirrors.html");
		HttpURLConnection con = (HttpURLConnection)(url.openConnection());
		System.setProperty("http.agent", ""); //Spoofing the user agent is required to track stats
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		mirrors.clear();
		String data = "";
		while ((data = in.readLine()) != null) {
			mirrors.add(data);
		}
		in.close();
	}

	public String getNativesUrl() {
		if (settings.checkProperty("latestLWJGL")) {
			if (settings.getPropertyBoolean("latestLWJGL")) {
				return latestLWJGLURL;
			}
		}
		return baseURL;
	}

	public String getNativesUrl(String fileName) {
		if (settings.checkProperty("latestLWJGL")) {
			if (settings.getPropertyBoolean("latestLWJGL")) {
				return latestLWJGLURL + fileName + ".zip";
			}
		}
		return baseURL + fileName + ".jar.lzma";
	}

	public boolean isLZMANatives() {
		if (settings.checkProperty("latestLWJGL")) {
			if (settings.getPropertyBoolean("latestLWJGL")) {
				return false;
			}
		}
		return true;
	}

	public String readVersionFile(File file) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		String version = dis.readUTF();
		dis.close();
		return version;
	}

	public void writeVersionFile(File file, String version) throws Exception {
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
		dos.writeUTF(version);
		dos.close();
	}

	public Boolean checkMCUpdate(File versionFile) throws Exception {
		if (!GameUpdater.binDir.exists())
			return true;
		if (!new File(binDir, "natives").exists())
			return true;
		if (!versionFile.exists())
			return true;
		long currentVersion = Long.parseLong(this.readVersionFile(versionFile));
		return this.latestVersion > currentVersion;
	}

	private void extractNatives(File nativesDir, File nativesJar) throws Exception {

		if (!nativesDir.exists())
			nativesDir.mkdir();

		JarFile jar = new JarFile(nativesJar);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (entry.isDirectory())
				continue;
			if (name.startsWith("META-INF"))
				continue;
			InputStream inputStream = jar.getInputStream(entry);
			File outFile = new File(nativesDir.getPath() + File.separator + name);
			if (!outFile.exists())
				outFile.createNewFile();
			OutputStream out = new FileOutputStream(new File(nativesDir.getPath() + File.separator + name));

			int read;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			inputStream.close();
			out.flush();
			out.close();
		}

	}

	private File getNatives() throws Exception {
		String osName = System.getProperty("os.name").toLowerCase();
		String fname;

		if (osName.contains("win")) {
			fname = "windows_natives";
		} else if (osName.contains("mac")) {
			fname = "macosx_natives";
		} else if (osName.contains("solaris") || osName.contains("sunos")) {
			fname = "solaris_natives";
		} else if (osName.contains("linux") || osName.contains("unix")) {
			fname = "linux_natives";
		} else {
			throw new UnsupportedOSException();
		}

		if (!updateDir.exists())
			updateDir.mkdir();

		this.downloadFile(getNativesUrl(fname), updateDir.getPath() + File.separator + (isLZMANatives() ? "natives.jar.lzma" : "natives.zip"));

		if (isLZMANatives())
			extractLZMA(GameUpdater.updateDir.getPath() + File.separator + "natives.jar.lzma", GameUpdater.updateDir.getPath() + File.separator + "natives.zip");

		return new File(updateDir.getPath() + File.separator + "natives.jar.lzma");
	}

	public void updateSpout() throws Exception {
		performBackup();

		if (GameUpdater.updateDir.exists())
			purgeDir(updateDir);
		GameUpdater.updateDir.mkdirs();

		File updateMC = new File(updateDir.getPath() + File.separator + "minecraft.jar");

		if (!updateMC.exists())
			downloadFile(baseURL + "minecraft.jar?user=" + user + "&ticket=" + downloadTicket, updateMC.getPath());

		File spout = new File(GameUpdater.updateDir.getPath() + File.separator + "spoutcraft.zip");

		if (devmode) {
			System.out.println(getDevelopmentBuildUrl());
			downloadFile(getDevelopmentBuildUrl(), spout.getPath());
		} else {
			downloadFile(getRecommendedBuildUrl(), spout.getPath());
		}

		this.unzipSpout();

		ArrayList<File> spoutMod = this.getFiles(new File(updateDir.getPath() + File.separator + "spoutcraft"));

		this.addFilesToExistingZip(updateMC, spoutMod, PlatformUtils.getWorkingDirectory() + File.separator + "temp" + File.separator + "spoutcraft" + File.separator);

		File mcJar = new File(binDir, "minecraft.jar");
		mcJar.delete();

		// Move file
		updateMC.renameTo(mcJar);

		if (GameUpdater.spoutcraftDir.exists())
			GameUpdater.spoutcraftDir.mkdir();

		File spoutVersion = new File(GameUpdater.spoutcraftDir.getPath() + File.separator + "versionSpoutcraft");
		if (spoutVersion.exists())
			spoutVersion.delete();

		this.writeFile(spoutVersion.getPath(), this.getSpoutVersion());
	}

	public String getSpoutVersion() throws Exception {
		String version;
		URL url;
		if (devmode) {
			url = new URL("http://ci.getspout.org/job/Spoutcraft/lastSuccessfulBuild/buildNumber");
		} else {
			url = new URL("http://ci.getspout.org/job/Spoutcraft/Recommended/buildNumber");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String str = in.readLine();
		if (str != null) {
			version = str;
			return version;
		}
		in.close();
		return null;
	}

	public boolean checkSpoutUpdate() throws Exception {
		if (!PlatformUtils.getWorkingDirectory().exists())
			return true;
		if (!GameUpdater.spoutcraftDir.exists())
			return true;
		File bcVersion = new File(GameUpdater.spoutcraftDir.getPath() + File.separator + "versionSpoutcraft");
		if (!bcVersion.exists())
			return true;
		BufferedReader br = new BufferedReader(new FileReader(bcVersion));
		String line;
		String version = null;
		if ((line = br.readLine()) != null) {
			version = line;
		}

		String latest = this.getSpoutVersion();

		if (latest == null)
			return false;
		if (version == null)
			return true;
		if (version.contains("."))
			return true;

		int c = Integer.parseInt(version);
		int l = Integer.parseInt(latest);

		return c < l || (c > l && !devmode);

	}

	public boolean allowUpdate() {
		if (allowUpdates == -1) {
			try {
				String version = null;
				URL url = new URL("http://dl.dropbox.com/u/27798409/AllowMC.txt");
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String str = in.readLine();
				if (str != null) {
					version = str;
					System.out.println(version);
				}
				in.close();

				if (version == null) {
					allowUpdates = 0;
					return false;
				}

				if (version.equalsIgnoreCase("true")) {
					allowUpdates = 1;
					return true;
				}
			} catch (Exception ex) {

			}
			allowUpdates = 0;
			return false;
		}
		return allowUpdates != 0;
	}

	public void unzipSpout() throws Exception {
		final int BUFFER = 2048;
		BufferedOutputStream dest;
		FileInputStream fis = new FileInputStream(new File(GameUpdater.updateDir.getPath() + File.separator + "spoutcraft.zip"));
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		File dir = new File(updateDir + File.separator + "spoutcraft");
		if (dir.exists()) {
			purgeDir(dir);
		}

		dir.mkdir();
		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte data[] = new byte[BUFFER];
			if (entry.isDirectory()) {
				File f2 = new File(dir.getPath() + File.separator + entry.getName());
				f2.mkdir();
			} else {
				FileOutputStream fos = new FileOutputStream(new File(dir.getPath() + File.separator + entry.getName()));
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		}
		zis.close();
		fis.close();
	}

	public void performBackup() throws Exception {
		File spoutVersion = new File(GameUpdater.spoutcraftDir.getPath() + File.separator + "versionSpoutcraft");
		if (!spoutVersion.exists())
			return;

		BufferedReader br;
		br = new BufferedReader(new FileReader(spoutVersion));
		String line;
		String version = null;

		if ((line = br.readLine()) != null) {
			version = line;
		}

		if (version == null)
			return;

		if (!backupDir.exists())
			backupDir.mkdir();

		File zip = new File(GameUpdater.backupDir, version + "-backup.zip");

		if (zip.exists())
			return;

		ArrayList<File> exclude = new ArrayList<File>();
		exclude.add(GameUpdater.backupDir);
		if (!(settings.checkProperty("worldbackup") && settings.getPropertyBoolean("worldbackup"))) {
			exclude.add(GameUpdater.savesDir);
		}
		exclude.add(GameUpdater.updateDir);
		exclude.add(SystemConsoleListener.logDir);

		zip.createNewFile();

		addFilesToExistingZip(zip, getFiles(PlatformUtils.getWorkingDirectory(), exclude), PlatformUtils.getWorkingDirectory() + File.separator);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean canPlayOffline() {
		try {
			String path = (String) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return PlatformUtils.getWorkingDirectory() + File.separator + "bin" + File.separator;
				}
			});
			File dir = new File(path);
			if (!dir.exists())
				return false;

			dir = new File(dir, "version");
			if (!dir.exists())
				return false;

			if (dir.exists()) {
				String version = readVersionFile(dir);
				if ((version != null) && (version.length() > 0))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public void addFilesToExistingZip(File zipFile, ArrayList<File> files, String rootDir) throws IOException {
		File tempFile = File.createTempFile(zipFile.getName(), null, zipFile.getParentFile());
		tempFile.delete();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(tempFile)));
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			for (File f : files) {
				String path = f.getPath();
				path = path.replace(rootDir, "");
				path = path.replaceAll("\\\\", "/");
				if (path.equals(name) || name.contains("META-INF")) {
					notInFiles = false;
					break;
				}
			}
			if (notInFiles) {
				out.putNextEntry(new ZipEntry(name));
				int len;
				while ((len = zin.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		zin.close();
		for (File file : files) {
			try {
				InputStream in = new FileInputStream(file);

				String path = file.getPath();
				path = path.replace(rootDir, "");
				path = path.replaceAll("\\\\", "/");
				out.putNextEntry(new ZipEntry(path));

				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				out.closeEntry();
				in.close();
			} catch (Exception e) {
			}
		}

		out.close();
	}

	// I know that is is not the best method but screw it, I am tired of trying to do it myself :P
	private void extractLZMA(String in, String out) throws Exception {
		String[] args = { "d", in, out };
		LzmaAlone.main(args);
	}

	@SuppressWarnings("unused")
	private void extractPack(String in, String out) throws Exception {
		File f = new File(in);
		if (!f.exists())
			return;

		FileOutputStream fostream = new FileOutputStream(out);
		JarOutputStream jostream = new JarOutputStream(fostream);

		Pack200.Unpacker unpacker = Pack200.newUnpacker();
		unpacker.unpack(f, jostream);
		jostream.close();

		f.delete();
	}

	public void writeFile(String out, String contents) {
		FileWriter fWriter;
		BufferedWriter writer;
		try {
			fWriter = new FileWriter(out);
			writer = new BufferedWriter(fWriter);
			System.out.print(contents);
			writer.write(contents);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadFile(String url, String outPut) throws Exception {
		Download download = new Download(url, outPut);
		download.setListener(this);
		download.run();
		if (!download.success) {
			throw new IOException();
		}
	}

	public ArrayList<File> getFiles(File dir) {
		return getFiles(dir, new ArrayList<File>());
	}

	public ArrayList<File> getFiles(File dir, ArrayList<File> exclude) {
		ArrayList<File> result = new ArrayList<File>();
		for (File file : dir.listFiles()) {
			if (!exclude.contains(dir)) {
				if (file.isDirectory()) {
					result.addAll(this.getFiles(file, exclude));
					continue;
				}
				result.add(file);
			}
		}
		return result;
	}

	public static void purgeDir(File file) {
		if (file.exists()) {
			if (file.isDirectory())
				deleteSubDir(file);
			file.delete();
		}
	}

	public static void deleteSubDir(File argFile) {
		for (File file : argFile.listFiles()) {
			if (file.isDirectory()) {
				deleteSubDir(file);
			}
			file.delete();
		}
	}

	public void stateChanged(String fileName, float progress) {
		this.listener.stateChanged(fileName, progress);
	}

	public void setListener(DownloadListener listener) {
		this.listener = listener;
	}
}
