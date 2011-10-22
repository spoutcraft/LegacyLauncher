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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.JProgressBar;

import org.spoutcraft.launcher.exception.BadLoginException;
import org.spoutcraft.launcher.exception.MCNetworkException;
import org.spoutcraft.launcher.exception.OutdatedMCLauncherException;
import org.spoutcraft.launcher.exception.MinecraftUserNotPremiumException;


public class MinecraftUtils {
	public static String server = null;
	public static String port = null;
	
	public static String[] doLogin(String user, String pass, JProgressBar progress) throws BadLoginException, MCNetworkException, OutdatedMCLauncherException, UnsupportedEncodingException, MinecraftUserNotPremiumException {
		    throw new MCNetworkException();
			/*String parameters = "user=" + URLEncoder.encode(user, "UTF-8") + "&password=" + URLEncoder.encode(pass, "UTF-8") + "&version=" + 13;
			String result = PlatformUtils.excutePost("https://login.minecraft.net/", parameters, progress);
			if (result == null) {
				throw new MCNetworkException();
			}
			if (!result.contains(":")) {
				if (result.trim().equals("Bad login")) {
					throw new BadLoginException();
                } else if (result.trim().equals("User not premium")) {
                    throw new MinecraftUserNotPremiumException();
				} else if (result.trim().equals("Old version")) {
					throw new OutdatedMCLauncherException();
				} else {
					System.err.print("Unknown login result: " + result);
				}
				throw new MCNetworkException();
			}
			return result.split(":");*/
	}
	
	public static void setServer(String server) {
		if (server.contains(":")) {
			String[] serv = server.split(":");
			MinecraftUtils.server = serv[0];
			MinecraftUtils.port = serv[1];
		}
	}
	
}
