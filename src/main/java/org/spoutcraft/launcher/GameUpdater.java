package org.spoutcraft.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.spoutcraft.launcher.Exceptions.UnsupportedOSException;



import SevenZip.LzmaAlone;


public class GameUpdater {
	
	public long latestVersion;
	public String user, downloadTicket;
	public Boolean force = false;
	public final File binDir = new File(PlatformUtils.getWorkingDirectory().getPath() +  File.separator + "bin");
	public final File updateDir = new File(PlatformUtils.getWorkingDirectory().getPath() +  File.separator + "updateFolder");
	public final File backupDir = new File(PlatformUtils.getWorkingDirectory().getPath() +  File.separator + "backups");
	public final File bcDir = new File(PlatformUtils.getWorkingDirectory().getPath() +  File.separator + "bukkitcontrib");
	public final String baseURL = "http://s3.amazonaws.com/MinecraftDownload/";
	public final String bcDownloadURL = "http://www.minedev.net/BukkitContrib/spoutcraft.zip";
	
	public GameUpdater(String user, String downloadTicket, String latestVersion) { 
		this.user = user;
		this.downloadTicket = downloadTicket;
		this.latestVersion = Long.parseLong(latestVersion);
	}
	
	public GameUpdater(String user, String downloadTicket, String latestVersion, Boolean forceUpdate) { 
		this.user = user;
		this.downloadTicket = downloadTicket;
		this.latestVersion = Long.parseLong(latestVersion);
		this.force = forceUpdate;
	}
	
	public boolean updateMC() throws Exception {
		System.out.print("Checking for Minecraft Update...\n");
		if (!this.mcUpdateAvailible(new File(this.binDir + File.separator + "version")) && !this.force) {
			System.out.print("Minecraft is up to date.\n");
			return false;
		}
		this.purgeDir(binDir);
		this.purgeDir(updateDir);
		
		binDir.mkdir();
		updateDir.mkdir();
		File nativesDir = new File(binDir.getPath() + File.separator + "natives");
		nativesDir.mkdir();
		
		// Processs minecraft.jar \\
		downloadFile(baseURL + "minecraft.jar?user=" + user + "&ticket=" + downloadTicket, this.updateDir + File.separator + "minecraft.jar");
		// Later in the process we will mod and move it
		
		// Process other Downloads
		downloadFile(baseURL + "jinput.jar", this.binDir.getPath() + File.separator + "jinput.jar");
		downloadFile(baseURL + "lwjgl.jar", this.binDir.getPath() + File.separator + "lwjgl.jar");
		downloadFile(baseURL + "lwjgl_util.jar", this.binDir.getPath() + File.separator + "lwjgl_util.jar");
		getNatives();
		
		// Unzip lzma \\
		/*extractLZMA(this.updateDir.getPath() + File.separator + "jinput.jar.pack.lzma" ,this.updateDir.getPath() + File.separator + "jinput.jar.pack");
		extractLZMA(this.updateDir.getPath() + File.separator + "lwjgl.jar.pack.lzma" ,this.updateDir.getPath() + File.separator + "lwjgl.jar.pack");
		extractLZMA(this.updateDir.getPath() + File.separator + "lwjgl_util.jar.pack.lzma" ,this.updateDir.getPath() + File.separator + "lwjgl_util.jar.pack");*/
		extractLZMA(this.updateDir.getPath() + File.separator + "natives.jar.lzma", this.updateDir.getPath() + File.separator + "natives.jar");
		
		// unzip pack and move to bin \\
		/*extractPack(this.updateDir.getPath() + File.separator + "jinput.jar.pack" ,this.binDir.getPath() + File.separator + "jinput.jar");
		extractPack(this.updateDir.getPath() + File.separator + "lwjgl.jar.pack" ,this.binDir.getPath() + File.separator + "lwjgl.jar");
		extractPack(this.updateDir.getPath() + File.separator + "lwjgl_util.jar.pack" ,this.binDir.getPath() + File.separator + "lwjgl_util.jar");*/
		
		// Extract Natives \\
		extractNatives(nativesDir, new File(this.updateDir.getPath() + File.separator + "natives.jar"));
		
		writeVersionFile(new File(this.binDir + File.separator + "version"), new Long(this.latestVersion).toString());
		
		return true;
	}
	
	public boolean updateBC(Boolean force) throws Exception {
		System.out.print("Checking for Spout update...\n");
		if (!this.checkBCUpdate() && !force) {
			System.out.print("Spout is up to date :)\n");
			return false;
		}
		
		if (!this.updateDir.exists()) this.updateDir.mkdir();
		
		if (!new File(this.updateDir.getPath() + File.separator + "minecraft.jar").exists()) downloadFile(baseURL + "minecraft.jar?user=" + user + "&ticket=" + downloadTicket, this.updateDir + File.separator + "minecraft.jar");
		
		File spout = new File(this.updateDir.getPath() + File.separator + "Spout.zip");
		
		downloadFile(bcDownloadURL, spout.getPath());
		
		this.unzipBC();
		
		ArrayList<File> spoutMod = this.getFiles(new File(updateDir.getPath() + File.separator + "Spout"));
		
		File updateMC = new File(updateDir.getPath() + File.separator + "minecraft.jar");
		
		this.addFilesToExistingZip(updateMC, spoutMod);
		
		//Move file
		updateMC.renameTo(new File(binDir, updateMC.getName()));
		
		return true;
	}
	
	private void purgeDir(File file) {
		File delFile = file;
		if (delFile.exists()) {
			if (delFile.isDirectory()) deleteSubDir(delFile);
			delFile.delete();
		}
	}
	
	private void deleteSubDir(File argFile) {
		for (File file : argFile.listFiles()) {
			if (file.isDirectory()) {
				this.deleteSubDir(file);
			}
			file.delete();
		}
	}
	
	@SuppressWarnings("unused")
	private void extractPack(String in, String out) throws Exception {
		File f = new File(in);
		if (!f.exists()) return;

		FileOutputStream fostream = new FileOutputStream(out);
		JarOutputStream jostream = new JarOutputStream(fostream);

		Pack200.Unpacker unpacker = Pack200.newUnpacker();
		unpacker.unpack(f, jostream);
		jostream.close();

		f.delete();
	 }
	
	private void downloadFile(String url, String outPut) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
		FileOutputStream fos = new FileOutputStream(outPut);
		BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
		byte[] data = new byte[1024];
		int x=0;
		while((x=in.read(data,0,1024))>=0)
		{
			bout.write(data,0,x);
		}
		bout.close();
		in.close();
	}
	
	//I know that is is not the best method but screw it, I am tired of trying to do it myself :P
	private void extractLZMA(String in, String out) throws Exception {
		String[] args = { "d", in, out };
		LzmaAlone.main(args);
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
	
	public Boolean mcUpdateAvailible(File versionFile) throws Exception {
		if (!versionFile.exists()) return true;
		long currentVersion = Long.parseLong(this.readVersionFile(versionFile));
		if (this.latestVersion > currentVersion) return true;
		return false;
	}
		
	private void extractNatives(File nativesDir, File nativesJar) throws Exception {
		
		if (!nativesDir.exists())nativesDir.mkdir();
		
		JarFile jar = new JarFile(nativesJar);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = (JarEntry)entries.nextElement();
			String name = entry.getName();
			if (entry.isDirectory()) continue;
			if (name.startsWith("META-INF")) continue;
			InputStream inputStream = jar.getInputStream(entry);
			File outFile = new File(nativesDir.getPath() + File.separator  + name);
			if (!outFile.exists()) outFile.createNewFile();
			OutputStream out = new FileOutputStream(new File(nativesDir.getPath() + File.separator  + name));
	 
			int read=0;
			byte[] bytes = new byte[1024];
	 
			while((read = inputStream.read(bytes))!= -1){
				out.write(bytes, 0, read);
			}
	 
			inputStream.close();
			out.flush();
			out.close();
		}
		
	}
	
	private File getNatives() throws Exception {
		String osName = System.getProperty("os.name").toLowerCase();
		String url = null;
	    if (osName.contains("win")) {
	    	url = "https://s3.amazonaws.com/MinecraftDownload/windows_natives.jar.lzma";
	    } else if (osName.contains("mac")) {
	    	url = "https://s3.amazonaws.com/MinecraftDownload/macosx_natives.jar.lzma";
	    } else if (osName.contains("solaris") || osName.contains("sunos")) {
	    	url = "https://s3.amazonaws.com/MinecraftDownload/solaris_natives.jar.lzma";
	    } else if (osName.contains("linux")) {
	    	url = "https://s3.amazonaws.com/MinecraftDownload/linux_natives.jar.lzma";
	    } else if (osName.contains("unix")) {
	    	url = "https://s3.amazonaws.com/MinecraftDownload/linux_natives.jar.lzma";
	    } else {
	    	throw new UnsupportedOSException();
	    }
	    
	    if (!updateDir.exists()) updateDir.mkdir();
	    this.downloadFile(url, updateDir.getPath() + File.separator + "natives.jar.lzma");
	    
	    return new File (updateDir.getPath() + File.separator + "natives.jar.lzma");
	}
	
	public ArrayList<File> getFiles(File dir) {
		ArrayList<File> result = new ArrayList<File>();
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				result.addAll(this.getFiles(file));
				continue;
			}
			result.add(file);
		}
		return result;
	}
	
	public void deleteFile(File argFile) {
		File delFile = argFile;
		if (delFile.exists()) delFile.delete();
	}
	
	public void addFilesToExistingZip(File zipFile, ArrayList<File> files) throws IOException {
        File tempFile = File.createTempFile(zipFile.getName(), null);
		tempFile.delete();

		boolean renameOk=zipFile.renameTo(tempFile);
		if (!renameOk)
		{
			throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];
		
		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			for (File f : files) {
				String path = f.getPath();
				path = path.replace(PlatformUtils.getWorkingDirectory() + File.separator + "updateFolder" + File.separator + "Spout" + File.separator, "");
				path = path.replaceAll("\\\\","/");
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
			InputStream in = new FileInputStream(file);

			String path = file.getPath();
			path = path.replace(PlatformUtils.getWorkingDirectory() + File.separator + "updateFolder" + File.separator + "Spout" + File.separator, "");
			path = path.replaceAll("\\\\","/");
			out.putNextEntry(new ZipEntry(path));

			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.closeEntry();
			in.close();
		}

		out.close();
		tempFile.delete();
	}

	// BukkitContrib Stuff \\
	public String getBCVersion() throws Exception {
		 String version = "-1";
		 URL url = new URL("http://dl.dropbox.com/u/49805/SpoutCraftVersion.txt");
		 BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		 String str;
		 while ((str = in.readLine()) != null) {
		      version = str;
		      return version;
		 }
		 in.close();
		 return null;
	 }
	
	private boolean checkBCUpdate() throws Exception {
		if (!PlatformUtils.getWorkingDirectory().exists()) return true;
		if (!this.bcDir.exists()) return true;
		
		File bcVersion = new File(this.bcDir.getPath() + File.separator + "version");
		
		if (!bcVersion.exists()) return true;
		
		Version ver = new Version(bcVersion);
		ver.read();
		if (!ver.compare(this.getBCVersion())) return true;
		
		return false;
	}
	
	public void unzipBC() throws Exception {
		final int BUFFER = 2048;
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(new File(this.updateDir.getPath() + File.separator + "Spout.zip"));
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));;
		ZipEntry entry;
		File dir = new File(updateDir + File.separator + "Spout");
		if (dir.exists()) {
			this.purgeDir(dir);
		}

		dir.mkdir();
		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte data[] = new byte[BUFFER];
			if(entry.isDirectory()) {
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
	
	public void write(Object msg) {
		System.out.print(msg);
		System.out.append("\n");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean canPlayOffline()
	{
		try
		{
			String path = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws Exception {
					return PlatformUtils.getWorkingDirectory() + File.separator + "bin" + File.separator;
				}
			});
			File dir = new File(path);
			if (!dir.exists()) return false;

			dir = new File(dir, "version");
			if (!dir.exists()) return false;

			if (dir.exists()) {
				String version = readVersionFile(dir);
				if ((version != null) && (version.length() > 0))
					return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

}
