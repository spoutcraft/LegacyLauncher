/*
 * This file is part of Spoutcraft Launcher.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft Launcher is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.skin.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.spoutcraft.launcher.api.Event;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.util.DownloadListener;
import org.spoutcraft.launcher.util.Utils;

public abstract class LoginFrame extends JFrame implements DownloadListener {
	private static final long serialVersionUID = -2105611446626766230L;
	protected Map<String, UserPasswordInformation> usernames = new HashMap<String, UserPasswordInformation>();
	protected boolean offline = false;

	public LoginFrame() {
		readSavedUsernames();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public final List<String> getSavedUsernames() {
		return new ArrayList<String>(usernames.keySet());
	}

	public final boolean hasSavedPassword(String user) {
		return (usernames.containsKey(user) && usernames.get(user) != null);
	}

	public final String getSavedPassword(String user) {
		UserPasswordInformation pass = usernames.get(user);
		if (!pass.isHash) {
			return pass.password;
		}
		return null;
	}

	public final String getSkinURL(String user) {
		for (String key : usernames.keySet()) {
			if (key.equalsIgnoreCase(user)) {
				UserPasswordInformation info = usernames.get(key);
				return "http://cdn.spout.org/legacy/skin/" + info.username + ".png";
			}
		}
		return "http://cdn.spout.org/legacy/skin/" + user + ".png";
	}

	public final String getUsername(String account) {
		for (String key : usernames.keySet()) {
			if (key.equalsIgnoreCase(account)) {
				UserPasswordInformation info = usernames.get(key);
				return info.username;
			}
		}
		return account;
	}
	
	public final boolean removeAccount(String account) {
		Iterator<Entry<String, UserPasswordInformation>>  i = usernames.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, UserPasswordInformation> e = i.next();
			if (e.getKey().equalsIgnoreCase(account)) {
				i.remove();
				return true;
			}
		}
		return false;
	}

	public final String getAccountName(String username) {
		for (Entry<String, UserPasswordInformation> e: usernames.entrySet()) {
			if (e.getValue().username.equals(username)) {
				return e.getKey();
			}
		}
		return username;
	}

	public final void saveUsername(String user, String pass) {
		if (!hasSavedPassword(user) && pass != null && !Utils.isEmpty(pass)) {
			usernames.put(user, new UserPasswordInformation(pass));
		}
	}

	protected final boolean canPlayOffline() {
		return offline;
	}

	public final void doLogin(String user) {
		if (!hasSavedPassword(user)) {
			throw new NullPointerException("There is no saved password for the user '" + user + "'");
		}
		doLogin(user, getSavedPassword(user));
	}

	public final void doLogin(String user, String pass) {
		if (pass == null) {
			throw new NullPointerException("The password was null when logging in as user: '" + user + "'");
		}

		Launcher.getGameUpdater().setDownloadListener(this);

		LoginWorker loginThread = new LoginWorker(this);
		loginThread.setUser(user);
		loginThread.setPass(pass);
		loginThread.execute();
	}

	@SuppressWarnings("unused")
	private final void readSavedUsernames() {
		try {
			File lastLogin = new File(Utils.getWorkingDirectory(), "lastlogin");
			if (!lastLogin.exists()) {
				return;
			}
			Cipher cipher = getCipher(2, "passwordfile");

			DataInputStream dis;
			if (cipher != null) {
				dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
			} else {
				dis = new DataInputStream(new FileInputStream(lastLogin));
			}

			try {
				while (true) {
					//read version
					int version = dis.readInt();
					//read key
					String key = dis.readUTF();
					//read user
					String user = dis.readUTF();
					//read hash
					boolean isHash = dis.readBoolean();
					if (isHash) {
						byte[] hash = new byte[32];
						dis.read(hash);
						usernames.put(key, new UserPasswordInformation(hash));
					} else {
						String pass = dis.readUTF();
						usernames.put(key, new UserPasswordInformation(pass));
					}
					UserPasswordInformation info = usernames.get(key);

					info.username = user;
				}
			} catch (EOFException e) {
			}
			dis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void writeUsernameList() {
		DataOutputStream dos = null;
		try {
			File lastLogin = new File(Utils.getWorkingDirectory(), "lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");

			if (cipher != null) {
				dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
			} else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
			}
			for (String user : usernames.keySet()) {
				UserPasswordInformation info = usernames.get(user);
				if (info.username == null) {
					info.username = user;
				}

				//version
				dos.writeInt(UserPasswordInformation.version);
				//key
				dos.writeUTF(user);
				//user
				dos.writeUTF(info.username);
				//password
				dos.writeBoolean(info.isHash);
				if (info.isHash) {
					dos.write(info.passwordHash);
				} else {
					dos.writeUTF(info.password);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) { }
			}
		}
	}

	private final static Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

	public abstract void init();

	public abstract JProgressBar getProgressBar();

	public abstract void onEvent(Event event);
	
	public abstract void disableForm();
	
	public abstract void enableForm();

	@SuppressWarnings("incomplete-switch")
	public final void onRawEvent(Event event) {
		switch (event) {
			case GAME_LAUNCH:
				setVisible(false);
				dispose();
				break;
			case SUCESSFUL_LOGIN:
				writeUsernameList();
				Launcher.getGameUpdater().runGame();
				break;
		}
		onEvent(event);
	}

	protected static final class UserPasswordInformation {
		public static final int version = 2;
		public String username = null;
		public boolean isHash;
		public byte[] passwordHash = null;
		public String password = null;

		public UserPasswordInformation(String pass) {
			isHash = false;
			password = pass;
		}

		public UserPasswordInformation(byte[] hash) {
			isHash = true;
			passwordHash = hash;
		}
	}
}
