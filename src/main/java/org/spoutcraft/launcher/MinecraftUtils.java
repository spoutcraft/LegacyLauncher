package org.spoutcraft.launcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.spoutcraft.launcher.Exceptions.BadLoginException;
import org.spoutcraft.launcher.Exceptions.MCNetworkException;
import org.spoutcraft.launcher.Exceptions.OutdatedMCLauncherException;


public class MinecraftUtils {

	public static String[] doLogin(String user, String pass) throws BadLoginException, MCNetworkException, OutdatedMCLauncherException, UnsupportedEncodingException {
		
			String parameters = "user=" + URLEncoder.encode(user, "UTF-8") + "&password=" + URLEncoder.encode(pass, "UTF-8") + "&version=" + 13;
			String result = PlatformUtils.excutePost("https://login.minecraft.net/", parameters);
			if (result == null) {
				throw new MCNetworkException();
			}
			if (!result.contains(":")) {
				if (result.trim().equals("Bad login")) {
					throw new BadLoginException();
				} else if (result.trim().equals("Old version")) {
					throw new OutdatedMCLauncherException();
				} else {
					System.out.print(result);
				}
				throw new MCNetworkException();
			}
			String[] values = result.split(":");
			//System.out.print(result);
			return values;
	}
	
}
