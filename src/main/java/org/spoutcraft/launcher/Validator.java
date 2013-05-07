/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the Spout License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Library;
import org.spoutcraft.launcher.rest.Minecraft;
import org.spoutcraft.launcher.util.MD5Utils;

public class Validator {
	private boolean passed = false;
	private boolean errors = false;

	public void run(SpoutcraftData build) {
		Launcher.getGameUpdater().setStartValidationTime(System.currentTimeMillis());
		try {
			errors = !validate(build);
		} catch (RestfulAPIException e) {
			e.printStackTrace();
		}
		Launcher.getGameUpdater().validationFinished(passed);
	}

	/**
	 * Returns true if validation completed without errors, false if something went wrong deleting files
	 * 
	 * @return true on validation completion, false on failure
	 * @throws RestfulAPIException 
	 */
	private boolean validate(SpoutcraftData build) throws RestfulAPIException {
		File minecraftJar = new File(Launcher.getGameUpdater().getBinDir(), "minecraft.jar");
		if (minecraftJar.exists()) {
			if (!compareMD5(build.getMinecraft().getMd5(), minecraftJar)) {
				//Launcher.err("Invalid minecraft.jar");
				return minecraftJar.delete();
			}
		} else {
			Launcher.err("There is no minecraft.jar!");
			return true;
		}

		File spoutcraft = new File(Launcher.getGameUpdater().getBinDir(), "spoutcraft.jar");
		if (spoutcraft.exists()) {
			if (!compareMD5(build.getMD5(), spoutcraft)) {
				Launcher.err("Invalid spoutcraft.jar");
				return spoutcraft.delete();
			}
		} else {
			Launcher.err("There is no spoutcraft.jar");
			return true;
		}


		File libDir = new File(Launcher.getGameUpdater().getBinDir(), "lib");

		List<Library> libraries = build.getLibraries();
		List<Library> allLibraries = new ArrayList<Library>();
		allLibraries.addAll(libraries);
		allLibraries.addAll(build.getMinecraft().getLibraries());

		for (Library lib : allLibraries) {
			File libraryFile = new File(libDir, lib.name() + ".jar");
			if ((lib.name().contains("lwjgl") || lib.name().contains("jinput"))&& !lib.getVersion().contains("natives")) {
				libraryFile = new File(Launcher.getGameUpdater().getBinDir(), lib.getArtifactId() + ".jar");
			} else if (lib.getVersion().contains("natives")) {
				continue;
			}

			if (libraryFile.exists()) {
				String md5 = MD5Utils.getMD5(libraryFile);
				if (!lib.valid(md5)) {
					Launcher.err("Invalid " + libraryFile.getName());
					return libraryFile.delete();
				}
			} else {
				Launcher.err("There is no " + libraryFile.getName());
				return true;
			}
		}
		passed = true;
		return true;
	}

	/**
	 * Returns true if the validator confirmed that all the files were correct
	 * 
	 * @return passed validation
	 */
	public boolean isValid() {
		return passed;
	}

	/**
	 * Returns true if the validator encountered an error while validating
	 * 
	 * @return true if an error occured
	 */
	public boolean hasErrors() {
		return errors;
	}

	private boolean compareMD5(String expected, File file) {
		String actual = MD5Utils.getMD5(file);
		Launcher.debug("Checking MD5 of " + file.getName() + ". Expected MD5: " + expected + " | Actual MD5: " + actual);
		if (expected == null || actual == null) {
			return false;
		}
		return expected.equals(actual);
	}
}
