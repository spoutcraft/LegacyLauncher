package org.spoutcraft.launcher.entrypoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.api.util.Download;
import org.spoutcraft.launcher.api.util.DownloadListener;
import org.spoutcraft.launcher.api.util.OperatingSystem;
import org.spoutcraft.launcher.api.util.Utils;

public class Start {
	
	public static void main(String[] args) throws Exception{
		//Test for exe relaunch
		SpoutcraftLauncher.setupLogger().info("Args: " + Arrays.toString(args));
		if (args.length > 0 && (args[0].equals("-Mover") || args[0].equals("-Launcher"))) {
			String[] argsCopy = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				argsCopy[i-1] = args[i];
			}
			if (args[0].equals("-Mover")) {
				Mover.main(argsCopy, true);
			} else {
				SpoutcraftLauncher.main(argsCopy);
			}
			return;
		}
		
		migrateFolders();

		int version = Integer.parseInt(SpoutcraftLauncher.getLauncherBuild());
		int latest = getLatestLauncherBuild();
		if (version < latest) {
			File codeSource = new File(Start.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			File temp;
			if (codeSource.getName().endsWith(".exe")) {
				temp = new File(Utils.getWorkingDirectory(), "temp.exe");
			} else {
				temp = new File(Utils.getWorkingDirectory(), "temp.jar");
			}

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
			
			ProgressSplashScreen splash = new ProgressSplashScreen();
			Download download;
			if (codeSource.getName().endsWith(".exe")) {
				download = new Download("http://build.spout.org/job/SpoutcraftLauncher/lastSuccessfulBuild/artifact/target/launcher-2.0.0-SNAPSHOT.exe", temp.getPath());
			} else {
				download = new Download("http://build.spout.org/job/SpoutcraftLauncher/lastSuccessfulBuild/artifact/target/launcher-2.0.0-SNAPSHOT.jar", temp.getPath());
			}

			download.setListener(new LauncherDownloadListener(splash));
			download.run();
			
			ProcessBuilder processBuilder = new ProcessBuilder();
			ArrayList<String> commands = new ArrayList<String>();
			if (!codeSource.getName().endsWith(".exe")) {
				if (OperatingSystem.getOS().isWindows()) {
					commands.add("javaw");
				} else {
					commands.add("java");
				}
				commands.add("-Xmx256m");
				commands.add("-cp");
				commands.add(temp.getAbsolutePath());
				commands.add(Mover.class.getName());
			} else {
				commands.add(temp.getAbsolutePath());
				commands.add("-Mover");
			}
			commands.add(codeSource.getAbsolutePath());
			commands.addAll(Arrays.asList(args));
			processBuilder.command(commands);

			try {
				processBuilder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		} else {
			SpoutcraftLauncher.main(args);
		}
	}

	public static int getLatestLauncherBuild() {
		BufferedReader in = null;
		try {
			URL url = new URL("http://build.spout.org/view/Legacy/job/SpoutcraftLauncher/lastSuccessfulBuild/buildNumber");
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				return Integer.parseInt(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) { }
			}
		}
		return 0;
	}

	private static void migrateFolders() {
		File brokenSpoutcraftDir = Utils.getWorkingDirectory("Spoutcraft");
		if (brokenSpoutcraftDir.exists()) {
			File correctSpoutcraftDir = Utils.getWorkingDirectory();
			OperatingSystem os = OperatingSystem.getOS();
			if (os.isUnix() || os.isMac()) {
				try {
					FileUtils.copyDirectory(brokenSpoutcraftDir, correctSpoutcraftDir);
					FileUtils.deleteDirectory(brokenSpoutcraftDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class LauncherDownloadListener implements DownloadListener {
		private final ProgressSplashScreen screen;
		LauncherDownloadListener(ProgressSplashScreen screen) {
			this.screen = screen;
		}

		public void stateChanged(String text, float progress) {
			screen.updateProgress((int)progress);
		}
	}
}
