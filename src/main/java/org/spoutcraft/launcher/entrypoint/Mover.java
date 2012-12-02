/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spoutcraft is licensed under the Spout License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.entrypoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import org.spoutcraft.launcher.util.Compatibility;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.Utils;

public class Mover {
	public static void main(String[] args) {
		main(args, false);
	}

	public static void main(String[] args, boolean exe) {
		try {
			SpoutcraftLauncher.setupLogger();
			execute(args, exe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	private static void execute(String[] args, boolean exe) throws Exception{
		File temp;
		if (exe) {
			temp = new File(Utils.getWorkingDirectory(), "temp.exe");
		} else {
			temp = new File(Utils.getWorkingDirectory(), "temp.jar");
		}
		File codeSource = new File(args[0]);
		codeSource.delete();
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(temp);
			fos = new FileOutputStream(codeSource);
			IOUtils.copy(fis, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		
		Compatibility.setExecutable(codeSource, true, true);

		ProcessBuilder processBuilder = new ProcessBuilder();
		ArrayList<String> commands = new ArrayList<String>();
		if (!exe) {
			if (OperatingSystem.getOS().isWindows()) {
				commands.add("javaw");
			} else {
				commands.add("java");
			}
			commands.add("-Xmx256m");
			commands.add("-cp");
			commands.add(codeSource.getAbsolutePath());
			commands.add(SpoutcraftLauncher.class.getName());
		} else {
			commands.add(temp.getAbsolutePath());
			commands.add("-Launcher");
		}
		commands.addAll(Arrays.asList(args));
		processBuilder.command(commands);

		processBuilder.start();
	}
}
