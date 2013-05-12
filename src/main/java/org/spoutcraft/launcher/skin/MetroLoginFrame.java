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
package org.spoutcraft.launcher.skin;

import static org.spoutcraft.launcher.util.ResourceUtils.getResourceAsStream;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.spout.downpour.connector.DefaultURLConnector;
import org.spout.downpour.connector.DownloadURLConnector;

import org.spoutcraft.launcher.rest.RestAPI;
import org.spoutcraft.launcher.skin.components.BackgroundImage;
import org.spoutcraft.launcher.skin.components.DynamicButton;
import org.spoutcraft.launcher.skin.components.FutureImage;
import org.spoutcraft.launcher.skin.components.HyperlinkJLabel;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.skin.components.LitePasswordBox;
import org.spoutcraft.launcher.skin.components.LiteProgressBar;
import org.spoutcraft.launcher.skin.components.LiteTextBox;
import org.spoutcraft.launcher.skin.components.LoginFrame;
import org.spoutcraft.launcher.skin.components.TransparentButton;
import org.spoutcraft.launcher.util.Compatibility;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.ResourceUtils;

public class MetroLoginFrame extends LoginFrame implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static final URL closeIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/close.png");
	private static final URL minimizeIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/minimize.png");
	private static final URL optionsIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/options.png");
    private static final URL youtubeIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/youtube.png");
	private static final URL youtubeHoverIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/youtube_hover.png");
	private static final URL twitterIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/twitter.png");
	private static final URL twitterHoverIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/twitter_hover.png");
	private static final URL facebookIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/facebook.png");
	private static final URL facebookHoverIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/facebook_hover.png");
	private static final URL steamIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/steam.png");
	private static final URL steamHoverIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/steam_hover.png");
	private static final URL gplusIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/gplus.png");
	private static final URL gplusHoverIcon = LoginFrame.class.getResource("/org/spoutcraft/launcher/resources/gplus_hover.png");
	private static final int FRAME_WIDTH = 880, FRAME_HEIGHT = 520;
	private static int mouseX = 0, mouseY = 0;
	private static final String CLOSE_ACTION = "close";
	private static final String MINIMIZE_ACTION = "minimize";
	private static final String OPTIONS_ACTION = "options";
    private static final String YOUTUBE_ACTION = "youtube";
	private static final String STEAM_ACTION = "steam";
	private static final String FACEBOOK_ACTION = "facebook";
	private static final String TWITTER_ACTION = "twitter";
	private static final String GPLUS_ACTION = "gplus";
	private static final String LOGIN_ACTION = "login";
	private static final String IMAGE_LOGIN_ACTION = "image_login";
	private static final String REMOVE_USER = "remove";
	private static final String TEXT_CHANGE_ACTION = "text_change";
	private static URI youtubeURL, twitterURL, facebookURL, gplusURL, steamURL;
	private final Map<JButton, DynamicButton> removeButtons = new HashMap<JButton, DynamicButton>();
	private LiteTextBox name;
	private LitePasswordBox pass;
	private LiteButton login;
	private JCheckBox remember;
	private TransparentButton close, minimize, options, youtube, gplus, steam, twitter, facebook;
	private LiteProgressBar progressBar;
	private OptionsMenu optionsMenu = null;
	public MetroLoginFrame() {
		initComponents();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().add(new BackgroundImage(FRAME_WIDTH, FRAME_HEIGHT));
	}

	private void initComponents() {
		Font minecraft = getMinecraftFont(12);

		int xShift = 0;
		int yShift = 0;
		if (this.isUndecorated()) {
			yShift += 30;
		}

		// Setup username box
		name = new LiteTextBox(this, "Username");
		name.setBounds(FRAME_WIDTH / 2 - 90, 339 + yShift, 180, 24);
		name.setFont(minecraft);
		name.addKeyListener(this);

		// Setup password box
		pass = new LitePasswordBox(this, "Password");
		pass.setBounds(FRAME_WIDTH / 2 - 90, 368 + yShift, 180, 24);
		pass.setFont(minecraft);
		pass.addKeyListener(this);

		// Setup remember checkbox
		remember = new JCheckBox("Remember");
		remember.setBounds(FRAME_WIDTH / 2 - 90, 397 + yShift, 110, 24);
		remember.setFont(minecraft);
		remember.setOpaque(false);
		remember.setBorderPainted(false);
		remember.setContentAreaFilled(false);
		remember.setBorder(null);
		remember.setForeground(Color.WHITE);
		remember.addKeyListener(this);

		// Setup login button
		login = new LiteButton("Login");
		login.setBounds(FRAME_WIDTH / 2 + 5, 397 + yShift, 85, 24);
		login.setFont(minecraft);
		login.setActionCommand(LOGIN_ACTION);
		login.addActionListener(this);
		login.addKeyListener(this);

		// Spoutcraft logo
		JLabel logo = new JLabel();
		logo.setBounds(FRAME_WIDTH / 2 - 200, 35, 400, 109);
		setIcon(logo, "spoutcraft.png", logo.getWidth(), logo.getHeight());

		// Progress Bar
		progressBar = new LiteProgressBar();
		progressBar.setBounds(FRAME_WIDTH / 2 - 192, pass.getY() + 90, 384, 23);
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setFont(minecraft);
		progressBar.setOpaque(true);
		progressBar.setTransparency(0.70F);
		progressBar.setHoverTransparency(0.70F);

		Font largerMinecraft;
		if (OperatingSystem.getOS().isUnix()) {
			largerMinecraft = minecraft.deriveFont((float)16);
		} else {
			largerMinecraft = minecraft.deriveFont((float)18);
		}

		// Home Link
		HyperlinkJLabel home = new HyperlinkJLabel("Home", "http://www.spout.org/");
		home.setToolTipText("Visit our homepage");
		home.setFont(largerMinecraft);
		home.setBounds(10, FRAME_HEIGHT - 27, 65, 20);
		home.setForeground(Color.WHITE);
		home.setOpaque(false);
		home.setTransparency(0.70F);
		home.setHoverTransparency(1F);

		// Forums link
		HyperlinkJLabel forums = new HyperlinkJLabel("Forums", "http://forums.spout.org/");
		forums.setToolTipText("Visit our community forums");
		forums.setFont(largerMinecraft);
		forums.setBounds(82, FRAME_HEIGHT - 27, 90, 20);
		forums.setForeground(Color.WHITE);
		forums.setOpaque(false);
		forums.setTransparency(0.70F);
		forums.setHoverTransparency(1F);

		// Donate link
		HyperlinkJLabel donate = new HyperlinkJLabel("Donate", "http://spout.in/donate");
		donate.setToolTipText("Donate to the project");
		donate.setFont(largerMinecraft);
		donate.setBounds(185, FRAME_HEIGHT - 27, 85, 20);
		donate.setForeground(Color.WHITE);
		donate.setOpaque(false);
		donate.setTransparency(0.70F);
		donate.setHoverTransparency(1F);

		// Close button
		close = new TransparentButton();
		close.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(closeIcon)));
		if (OperatingSystem.getOS().isMac()) {
			close.setBounds(0, 0, 37, 20);
		} else {
			close.setBounds(FRAME_WIDTH - 37, 0, 37, 20);
		}
		close.setTransparency(0.70F);
		close.setHoverTransparency(1F);
		close.setActionCommand(CLOSE_ACTION);
		close.addActionListener(this);
		close.setBorder(BorderFactory.createEmptyBorder());
		close.setContentAreaFilled(false);

		// Minimize button
		minimize = new TransparentButton();
		minimize.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(minimizeIcon)));
		if (OperatingSystem.getOS().isMac()) {
			minimize.setBounds(37, 0, 37, 20);
		} else {
			minimize.setBounds(FRAME_WIDTH - 74, 0, 37, 20);
		}
		minimize.setTransparency(0.70F);
		minimize.setHoverTransparency(1F);
		minimize.setActionCommand(MINIMIZE_ACTION);
		minimize.addActionListener(this);
		minimize.setBorder(BorderFactory.createEmptyBorder());
		minimize.setContentAreaFilled(false);

		// Options Button
		options = new TransparentButton();
		options.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(optionsIcon)));
		if (OperatingSystem.getOS().isMac()) {
			options.setBounds(74, 0, 37, 20);
		} else {
			options.setBounds(FRAME_WIDTH - 111, 0, 37, 20);
		}
		options.setTransparency(0.70F);
		options.setHoverTransparency(1F);
		options.setActionCommand(OPTIONS_ACTION);
		options.addActionListener(this);
		options.setBorder(BorderFactory.createEmptyBorder());
		options.setContentAreaFilled(false);

		// Steam button
		steam = new TransparentButton();
		steam.setToolTipText("Game with us on Steam");
		steam.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(steamIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		steam.setBounds(FRAME_WIDTH - 35, FRAME_HEIGHT - 32, 30, 30);
		steam.setTransparency(0.70F);
		steam.setHoverTransparency(1F);
		steam.setActionCommand(STEAM_ACTION);
		steam.addActionListener(this);
		steam.setBorder(BorderFactory.createEmptyBorder());
		steam.setContentAreaFilled(false);
		steam.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(steamHoverIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

		// Facebook button
		facebook = new TransparentButton();
		facebook.setToolTipText("Like us on Facebook");
		facebook.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(facebookIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		facebook.setBounds(FRAME_WIDTH - 70, FRAME_HEIGHT - 32, 30, 30);
		facebook.setTransparency(0.70F);
		facebook.setHoverTransparency(1F);
		facebook.setActionCommand(FACEBOOK_ACTION);
		facebook.addActionListener(this);
		facebook.setBorder(BorderFactory.createEmptyBorder());
		facebook.setContentAreaFilled(false);
		facebook.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(facebookHoverIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

		// Twitter button
		twitter = new TransparentButton();
		twitter.setToolTipText("Follow us on Twitter");
		twitter.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(twitterIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		twitter.setBounds(FRAME_WIDTH - 105, FRAME_HEIGHT - 32, 30, 30);
		twitter.setTransparency(0.70F);
		twitter.setHoverTransparency(1F);
		twitter.setActionCommand(TWITTER_ACTION);
		twitter.addActionListener(this);
		twitter.setBorder(BorderFactory.createEmptyBorder());
		twitter.setContentAreaFilled(false);
		twitter.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(twitterHoverIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

		// Google+ button
		gplus = new TransparentButton();
		gplus.setToolTipText("Follow us on Google+");
		gplus.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(gplusIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		gplus.setBounds(FRAME_WIDTH - 140, FRAME_HEIGHT - 32, 30, 30);
		gplus.setTransparency(0.70F);
		gplus.setHoverTransparency(1F);
		gplus.setActionCommand(GPLUS_ACTION);
		gplus.addActionListener(this);
		gplus.setBorder(BorderFactory.createEmptyBorder());
		gplus.setContentAreaFilled(false);
		gplus.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(gplusHoverIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

		// YouTube button
		youtube = new TransparentButton();
		youtube.setToolTipText("Subscribe to our videos");
		youtube.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(youtubeIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
		youtube.setBounds(FRAME_WIDTH - 175, FRAME_HEIGHT - 32, 30, 30);
		youtube.setTransparency(0.70F);
		youtube.setHoverTransparency(1F);
		youtube.setActionCommand(YOUTUBE_ACTION);
		youtube.addActionListener(this);
		youtube.setBorder(BorderFactory.createEmptyBorder());
		youtube.setContentAreaFilled(false);
		youtube.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(youtubeHoverIcon).getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

		// Rectangle
		JLabel bottomRectangle = new JLabel();
		bottomRectangle.setBounds(0, FRAME_HEIGHT - 34, FRAME_WIDTH, 34);
		bottomRectangle.setBackground(new Color(30, 30, 30, 180));
		bottomRectangle.setOpaque(true);

		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		java.util.List<String> savedUsers = getSavedUsernames();
		int users = Math.min(5, this.getSavedUsernames().size());
		for (int i = 0; i < users; i++) {
			String accountName = savedUsers.get(i);
			String userName = this.getUsername(accountName);
			
			if (i == 0) {
				name.setText(accountName);
				pass.setText(this.getSavedPassword(accountName));
				remember.setSelected(true);
			}

			// Create callable
			CallbackTask callback = getImage(userName);

			// Start callable
			FutureTask<BufferedImage> futureImage = new FutureTask<BufferedImage>(callback);
			Thread downloadThread = new Thread(futureImage, "Image download thread");
			downloadThread.setDaemon(true);
			downloadThread.start();

			// Create future image, using default mc avatar for now
			FutureImage userImage = new FutureImage(getDefaultImage());
			callback.setCallback(userImage);

			DynamicButton userButton = new DynamicButton(this, userImage, 44, accountName, userName);
			userButton.setFont(minecraft.deriveFont(14F));

			userImage.setRepaintCallback(userButton);

			userButton.setBounds((FRAME_WIDTH - 90) * (i + 1) / (users + 1), (FRAME_HEIGHT - 110) / 2 , 90, 90);
			contentPane.add(userButton);
			userButton.setActionCommand(IMAGE_LOGIN_ACTION);
			userButton.addActionListener(this);
			setIcon(userButton.getRemoveIcon(), "remove.png", 16);
			userButton.getRemoveIcon().addActionListener(this);
			userButton.getRemoveIcon().setActionCommand(REMOVE_USER);
			userButton.getRemoveIcon().setBorder(BorderFactory.createEmptyBorder());
			userButton.getRemoveIcon().setContentAreaFilled(false);
			removeButtons.put(userButton.getRemoveIcon(), userButton);
		}

		contentPane.add(name);
		contentPane.add(pass);
		contentPane.add(remember);
		contentPane.add(login);
		contentPane.add(steam);
		contentPane.add(twitter);
		contentPane.add(facebook);
		contentPane.add(gplus);
		contentPane.add(youtube);
		contentPane.add(home);
		contentPane.add(forums);
		contentPane.add(donate);
		contentPane.add(logo);
		contentPane.add(options);
		contentPane.add(close);
		contentPane.add(minimize);
		contentPane.add(progressBar);
		contentPane.add(bottomRectangle);
		setUndecorated(true);
		setFocusTraversalPolicy(new LoginFocusTraversalPolicy());
	}

	private void setIcon(JButton button, String iconName, int size) {
		try {
			button.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), size, size)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setIcon(JLabel label, String iconName, int w, int h) {
		try {
			label.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BufferedImage getDefaultImage() {
		try {
			return ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/face.png"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read backup image");
		}
	}

	private CallbackTask getImage(final String user) {
		return new CallbackTask(new Callable<BufferedImage>() {
			public BufferedImage call() throws Exception {
				try {
					System.out.println("Attempting to grab avatar helm for " + user + "...");
					InputStream stream = RestAPI.getCache().get(new URL("https://minotar.net/helm/" + user + "/100"), new DownloadURLConnector() {
						@Override
						public void setHeaders(URLConnection conn) {
							conn.setDoInput(true);
							conn.setDoOutput(false);
							System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
							conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
							HttpURLConnection.setFollowRedirects(true);
							conn.setUseCaches(false);
							((HttpURLConnection)conn).setInstanceFollowRedirects(true);
							conn.setConnectTimeout(10000);
							conn.setReadTimeout(10000);
						}
					}, true);
					BufferedImage image = ImageIO.read(stream);
					if (image == null) {
						throw new NullPointerException("No avatar helm downloaded!");
					}
					System.out.println("Completed avatar helm request!");
					return image;
				} catch (Exception e) {
					System.out.println("Failed avatar helm request!");
					throw e;
				}
			}
		});
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		this.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	public void mouseMoved(MouseEvent e) {
	}

	private static class CallbackTask implements Callable<BufferedImage> {
		private final Callable<BufferedImage> task;
		private volatile ImageCallback callback;
		CallbackTask(Callable<BufferedImage> task) {
			this.task = task;
		}

		public void setCallback(ImageCallback callback) {
			this.callback = callback;
		}

		public BufferedImage call() throws Exception {
			BufferedImage image = null;
			try {
				image = task.call();
				return image;
			} finally {
				callback.done(image);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent)e.getSource());
		}
	}

	private void action(String action, JComponent c) {
		if (action.equals(CLOSE_ACTION)) {
			System.exit(EXIT_ON_CLOSE);
		} else if (action.equals(MINIMIZE_ACTION)) {
		    setState(Frame.ICONIFIED);
		} else if (action.equals(OPTIONS_ACTION)) {
			if (optionsMenu == null || !optionsMenu.isVisible()) {
				optionsMenu = new OptionsMenu();
				optionsMenu.setModal(true);
				optionsMenu.setVisible(true);
			}
		} else if (action.equals(LOGIN_ACTION)) {
			String pass = new String(this.pass.getPassword());
			this.doLogin(getSelectedUser(), pass);
			if (remember.isSelected()) {
				saveUsername(getSelectedUser(), pass);
			}
		} else if (action.equals(IMAGE_LOGIN_ACTION)) {
			DynamicButton userButton = (DynamicButton)c;
			userButton.setEnabled(false);
			this.name.setText(userButton.getAccount());
			this.pass.setText(this.getSavedPassword(userButton.getAccount()));
			this.remember.setSelected(true);
			action(LOGIN_ACTION, userButton);
		}  else if (action.equals(REMOVE_USER)) {
			DynamicButton userButton = removeButtons.get((JButton)c);
			if (userButton.getRemoveIcon().getTransparency() > 0.1F) {
				this.removeAccount(userButton.getAccount());
				userButton.setVisible(false);
				userButton.setEnabled(false);
				getContentPane().remove(userButton);
				c.setVisible(false);
				c.setEnabled(false);
				getContentPane().remove(c);
				removeButtons.remove(c);
				writeUsernameList();
			}
		} else if (action.equals(STEAM_ACTION)) {
			try {
				steamURL = new URI("http://spout.in/steam");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Compatibility.browse(steamURL);
		} else if (action.equals(FACEBOOK_ACTION)) {
			try {
				facebookURL = new URI("http://spout.in/facebook");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Compatibility.browse(facebookURL);
		} else if (action.equals(TWITTER_ACTION)) {
			try {
				twitterURL = new URI("http://spout.in/twitter");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Compatibility.browse(twitterURL);
		}  else if (action.equals(GPLUS_ACTION)) {
			try {
				gplusURL = new URI("http://spout.in/gplus");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Compatibility.browse(gplusURL);
		}  else if (action.equals(YOUTUBE_ACTION)) {
			try {
				youtubeURL = new URI("http://spout.in/youtube");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Compatibility.browse(youtubeURL);
		}
	}

	public void stateChanged(final String status, final float progress) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int intProgress = Math.round(progress);
				progressBar.setValue(intProgress);
				String text = status;
				if (text.length() > 60) {
					text = text.substring(0, 60) + "...";
				}
				progressBar.setString(intProgress + "% " + text);
			}
		});
	}

	@Override
	public JProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	public void disableForm() {
	}

	@Override
	public void enableForm() {
	}

	@Override
	public String getSelectedUser() {
		return this.name.getText();
	}

	// Emulates tab focus policy of name -> pass -> remember -> login
	private class LoginFocusTraversalPolicy extends FocusTraversalPolicy {
		public Component getComponentAfter(Container con, Component c) {
			if (c == name) {
				return pass;
			} else if (c == pass) {
				return remember;
			} else if (c == remember) {
				return login;
			} else if (c == login) {
				return name;
			}
			return getFirstComponent(con);
		}

		public Component getComponentBefore(Container con, Component c) {
			if (c == name) {
				return login;
			} else if (c == pass) {
				return name;
			} else if (c == remember) {
				return pass;
			} else if (c == login) {
				return remember;
			}
			return getFirstComponent(con);
		}

		public Component getFirstComponent(Container c) {
			return name;
		}

		public Component getLastComponent(Container c) {
			return login;
		}

		public Component getDefaultComponent(Container c) {
			return name;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// Allows the user to press enter and log in from the login box focus, username box focus, or password box focus
			if (e.getComponent() == login || e.getComponent() == name || e.getComponent() == pass) {
				action(LOGIN_ACTION, (JComponent) e.getComponent());
			} else if (e.getComponent() == remember) {
				remember.setSelected(!remember.isSelected());
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}
}
