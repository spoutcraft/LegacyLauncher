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
package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.spoutcraft.launcher.*;
import org.spoutcraft.launcher.async.Download;
import org.spoutcraft.launcher.async.DownloadListener;
import org.spoutcraft.launcher.exception.BadLoginException;
import org.spoutcraft.launcher.exception.MCNetworkException;
import org.spoutcraft.launcher.exception.MinecraftUserNotPremiumException;
import org.spoutcraft.launcher.exception.OutdatedMCLauncherException;

public class LoginForm extends JFrame implements ActionListener, DownloadListener, KeyListener, WindowListener {
	private static final long serialVersionUID = -192904429165686059L;

	private static final class UserPasswordInformation {
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

	private JPanel contentPane;
	private JPasswordField passwordField;
	private JComboBox usernameField = new JComboBox();
	private JButton loginButton = new JButton("Login");
	JButton optionsButton = new JButton("Options");
	private JCheckBox rememberCheckbox = new JCheckBox("Remember");
	private JButton loginSkin1;
	private List<JButton> loginSkin1Image;
	private JButton loginSkin2;
	private List<JButton> loginSkin2Image;
	public final JProgressBar progressBar;
	HashMap<String, UserPasswordInformation> usernames = new HashMap<String, UserPasswordInformation>();
	public Boolean mcUpdate = false;
	public Boolean spoutUpdate = false;
	public static UpdateDialog updateDialog;
	private static String pass = null;
	public static String[] values = null;
	public static File clearCacheMarker = new File(PlatformUtils.getWorkingDirectory(), "ctemp");

	public static final GameUpdater gu = new GameUpdater();
	private SettingsHandler settings = new SettingsHandler("defaults/spoutcraft.properties", new File(PlatformUtils.getWorkingDirectory(), "spoutcraft" + File.separator + "spoutcraft.properties"));
	OptionDialog options = new OptionDialog();

	Container loginPane = new Container();
	Container offlinePane = new Container();

	public LoginForm() {
		LoginForm.updateDialog = new UpdateDialog(this);
		settings.load();
		gu.setListener(this);

		this.addWindowListener(this);

		options.setVisible(false);
		loginButton.setFont(new Font("Arial", Font.PLAIN, 11));
		loginButton.setBounds(272, 13, 86, 23);
		loginButton.setOpaque(false);
		loginButton.addActionListener(this);
		loginButton.setEnabled(false); // disable until login info is read
		optionsButton.setFont(new Font("Arial", Font.PLAIN, 11));
		optionsButton.setOpaque(false);
		optionsButton.addActionListener(this);
		usernameField.setFont(new Font("Arial", Font.PLAIN, 11));
		usernameField.addActionListener(this);
		usernameField.setOpaque(false);

		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));
		setResizable(false);

		setTitle("Spoutcraft Launcher");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - 860) / 2, (dim.height - 500) / 2, 860, 500);

		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(8, 0, 294, 99);
		lblLogo.setIcon(new ImageIcon(LoginForm.class.getResource("/org/spoutcraft/launcher/spoutcraft.png")));

		JLabel lblMinecraftUsername = new JLabel("Minecraft Username: ");
		lblMinecraftUsername.setFont(new Font("Arial", Font.PLAIN, 11));
		lblMinecraftUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinecraftUsername.setBounds(-17, 17, 150, 14);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setFont(new Font("Arial", Font.PLAIN, 11));
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(33, 42, 100, 20);

		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Arial", Font.PLAIN, 11));
		passwordField.setBounds(143, 42, 119, 22);

		loginSkin1 = new JButton("Login as Player");
		loginSkin1.setFont(new Font("Arial", Font.PLAIN, 11));
		loginSkin1.setBounds(72, 428, 119, 23);
		loginSkin1.setOpaque(false);
		loginSkin1.addActionListener(this);
		loginSkin1.setVisible(false);
		loginSkin1Image = new ArrayList<JButton>();

		loginSkin2 = new JButton("Login as Player");
		loginSkin2.setFont(new Font("Arial", Font.PLAIN, 11));
		loginSkin2.setBounds(261, 428, 119, 23);
		loginSkin2.setOpaque(false);
		loginSkin2.addActionListener(this);
		loginSkin2.setVisible(false);
		loginSkin2Image = new ArrayList<JButton>();

		progressBar = new JProgressBar();
		progressBar.setBounds(30, 100, 400, 23);
		progressBar.setVisible(false);
		progressBar.setStringPainted(true);
		progressBar.setOpaque(true);

		readUsedUsernames();

		JLabel purchaseAccount = new HyperlinkJLabel("<html><u>Need a minecraft account?</u></html>", "http://www.minecraft.net/register.jsp");
		purchaseAccount.setHorizontalAlignment(SwingConstants.RIGHT);
		purchaseAccount.setBounds(243, 70, 111, 14);

		purchaseAccount.setText("<html><u>Need an account?</u></html>");
		purchaseAccount.setFont(new Font("Arial", Font.PLAIN, 11));
		purchaseAccount.setForeground(new Color(0, 0, 255));
		usernameField.setBounds(143, 14, 119, 25);
		rememberCheckbox.setFont(new Font("Arial", Font.PLAIN, 11));

		rememberCheckbox.setOpaque(false);

		final JTextPane editorPane = new JTextPane();
		editorPane.setContentType("text/html");

		final JLabel loadingEditor = new JLabel("Loading News Feed...");
		loadingEditor.setBounds(422, 320, 10, 50);
		loadingEditor.setVisible(false);

		SwingWorker<Object, Object> newsThread = new SwingWorker<Object, Object>() {

			protected Object doInBackground() throws Exception {
				try {
					URL url = new URL("http://updates.getspout.org/");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
						editorPane.setVisible(false);
						loadingEditor.setVisible(true);
						editorPane.setPage(url);
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
						}
						String text = editorPane.getText();
						int index = text.indexOf("<!-- BEGIN TUMBLR CODE -->");
						int endIndex = text.indexOf("<!-- END TUMBLR CODE -->") + "<!-- END TUMBLR CODE -->".length();
						if (index > -1 && endIndex > -1) {
							text = text.substring(0, index) + text.substring(endIndex);
						}
						text = text.replaceAll("<li>", "- ");
						text = text.replaceAll("</li>", "<br/>");
						text = text.replaceAll("<p>", "");
						text = text.replace("</p>", "<br/>");
						text = text.replaceAll("</p>", "<br/><br/>");
						editorPane.setText(text);
						loadingEditor.setVisible(false);
						editorPane.setVisible(true);
					} else {
						editorPane.setText("Oh Noes! Our Tumblr Feed is Down!");
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				return null;
			}
		};
		newsThread.execute();

		editorPane.setEditable(false);
		editorPane.setOpaque(false);

		JLabel trans2;

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBounds(473, 11, 372, 340);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);

		editorPane.setCaretPosition(0);
		trans2 = new JLabel();
		trans2.setBackground(new Color(229, 246, 255, 100));
		trans2.setOpaque(true);
		trans2.setBounds(473, 11, 372, 340);

		JLabel login = new JLabel();
		login.setBackground(new Color(255, 255, 255, 120));
		login.setOpaque(true);
		login.setBounds(473, 362, 372, 99);

		JLabel trans;
		trans = new JLabel();
		trans.setBackground(new Color(229, 246, 255, 60));
		trans.setOpaque(true);
		trans.setBounds(0, 0, 854, 480);

		usernameField.getEditor().addActionListener(this);
		passwordField.addKeyListener(this);
		rememberCheckbox.addKeyListener(this);

		usernameField.setEditable(true);
		contentPane.setLayout(null);
		rememberCheckbox.setBounds(144, 66, 93, 23);
		contentPane.add(lblLogo);
		optionsButton.setBounds(272, 41, 86, 23);
		contentPane.add(loginSkin1);
		contentPane.add(loginSkin2);

		loginPane.setBounds(473, 362, 372, 99);
		loginPane.add(lblPassword);
		loginPane.add(lblMinecraftUsername);
		loginPane.add(passwordField);
		loginPane.add(usernameField);
		loginPane.add(loginButton);
		loginPane.add(rememberCheckbox);
		loginPane.add(purchaseAccount);
		loginPane.add(optionsButton);
		contentPane.add(loginPane);

		JLabel offlineMessage = new JLabel("Could not connect to minecraft.net");
		offlineMessage.setFont(new Font("Arial", Font.PLAIN, 14));
		offlineMessage.setBounds(25, 40, 217, 17);

		JButton tryAgain = new JButton("Try Again");
		tryAgain.setOpaque(false);
		tryAgain.setFont(new Font("Arial", Font.PLAIN, 12));
		tryAgain.setBounds(257, 20, 100, 25);

		JButton offlineMode = new JButton("Offline Mode");
		offlineMode.setOpaque(false);
		offlineMode.setFont(new Font("Arial", Font.PLAIN, 12));
		offlineMode.setBounds(257, 52, 100, 25);

		offlinePane.setBounds(473, 362, 372, 99);
		offlinePane.add(tryAgain);
		offlinePane.add(offlineMode);
		offlinePane.add(offlineMessage);
		offlinePane.setVisible(false);
		contentPane.add(offlinePane);

		contentPane.add(scrollPane);
		contentPane.add(trans2);
		contentPane.add(login);
		contentPane.add(trans);
		contentPane.add(progressBar);

		final JLabel background = new JLabel("Loading...");
		background.setVerticalAlignment(SwingConstants.CENTER);
		background.setHorizontalAlignment(SwingConstants.CENTER);
		background.setBounds(0, 0, 854, 480);
		contentPane.add(background);

		try {
			final File bgCache;
			bgCache = new File(PlatformUtils.getWorkingDirectory(), "launcher_cache.jpg");
			SwingWorker<Object, Object> bgThread = new SwingWorker<Object, Object>() {

				protected Object doInBackground() throws MalformedURLException {
					if (!bgCache.exists() || System.currentTimeMillis() - bgCache.lastModified() > 1000 * 60 * 60 * 24) {
						Download download = new Download("http://www.getspout.org/splash/index.php", bgCache.getPath());
						download.run();
					}
					return null;
				}

				protected void done() {
					background.setIcon(new ImageIcon(bgCache.getPath()));
					background.setVerticalAlignment(SwingConstants.TOP);
					background.setHorizontalAlignment(SwingConstants.LEFT);
				}
			};
			bgThread.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Vector<Component> order = new Vector<Component>(5);
		order.add(usernameField.getEditor().getEditorComponent());
		order.add(passwordField);
		order.add(rememberCheckbox);
		order.add(loginButton);
		order.add(optionsButton);

		setFocusTraversalPolicy(new SpoutFocusTraversalPolicy(order));

		loginButton.setEnabled(true); // enable once logins are read

		if (clearCacheMarker.exists()) {
			clearCacheMarker.delete();
			OptionDialog.clearCache();
			doLogin(true);
		}
	}

	public void drawCharacter(String url, int x, int y, List<JButton> buttons) {
		BufferedImage originalImage;
		try {
			try {
				originalImage = ImageIO.read(new URL(url));
			} catch (Exception e) {
				originalImage = ImageIO.read(new URL("http://s3.amazonaws.com/MinecraftSkins/char.png"));
			}
			int type = BufferedImage.TYPE_INT_ARGB;// originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

			buttons.add(drawCropped(originalImage, type, 40, 8, 48, 16, x - 4, y - 5, 8)); // HAT

			buttons.add(drawCropped(originalImage, type, 8, 8, 16, 16, x, y, 7)); // HEAD

			buttons.add(drawCropped(originalImage, type, 20, 20, 28, 32, x, y + 56, 7)); // BODY

			buttons.add(drawCropped(originalImage, type, 44, 20, 48, 32, x - 28, y + 56, 7)); // ARMS
			buttons.add(drawCropped(originalImage, type, 44, 20, 48, 32, x + 56, y + 56, 7, true));

			buttons.add(drawCropped(originalImage, type, 4, 20, 8, 32, x, y + 140, 7)); // LEGS
			buttons.add(drawCropped(originalImage, type, 4, 20, 8, 32, x + 28, y + 140, 7, true));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JButton drawCropped(BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale) {
		return drawCropped(img, type, sx1, sy1, sx2, sy2, x, y, scale, false);
	}

	public JButton drawCropped(BufferedImage img, int type, int sx1, int sy1, int sx2, int sy2, int x, int y, int scale, boolean reflect) {
		BufferedImage resizedImage = new BufferedImage((sx2 - sx1) * scale, (sy2 - sy1) * scale, type);
		Graphics2D g = resizedImage.createGraphics();
		int asx2 = sx2, asx1 = sx1;
		if (reflect) {
			asx2 = sx1;
			asx1 = sx2;
		}
		g.drawImage(img, 0, 0, (sx2 - sx1) * scale, (sy2 - sy1) * scale, asx1, sy1, asx2, sy2, null);
		g.dispose();

		JButton tmp = new JButton(new ImageIcon(resizedImage));
		tmp.setSelectedIcon(tmp.getIcon());
		tmp.setDisabledIcon(tmp.getPressedIcon());
		tmp.setPressedIcon(tmp.getIcon());

		tmp.setOpaque(false);
		tmp.setFocusable(false);

		tmp.setContentAreaFilled(false);
		tmp.setBorderPainted(false);
		tmp.setRolloverEnabled(false);

		tmp.setBounds(x, y, (sx2 - sx1) * scale, (sy2 - sy1) * scale);
		tmp.addActionListener(this);
		contentPane.add(tmp);
		return tmp;
	}

	public void stateChanged(String fileName, float progress) {
		int intProgress = Math.round(progress);

		progressBar.setValue(intProgress);
		if (fileName.length() > 60) {
			fileName = fileName.substring(0, 60) + "...";
		}
		progressBar.setString(intProgress + "% " + fileName);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (loginButton.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			doLogin();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	private void readUsedUsernames() {
		int i = 0;
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");
			if (!lastLogin.exists())
				return;
			Cipher cipher = getCipher(2, "passwordfile");

			DataInputStream dis;
			if (cipher != null)
				dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
			else {
				dis = new DataInputStream(new FileInputStream(lastLogin));
			}

			try {
				// noinspection InfiniteLoopStatement
				while (true) {
					String user = dis.readUTF();
					boolean isHash = dis.readBoolean();
					if (isHash) {
						byte[] hash = new byte[32];
						dis.read(hash);

						usernames.put(user, new UserPasswordInformation(hash));
					} else {
						String pass = dis.readUTF();
						if (!pass.isEmpty()) {
							i++;
							if (i == 1) {
								loginSkin1.setText(user);
								loginSkin1.setVisible(true);
								drawCharacter("http://s3.amazonaws.com/MinecraftSkins/" + user + ".png", 103, 170, loginSkin1Image);
							} else if (i == 2) {
								loginSkin2.setText(user);
								loginSkin2.setVisible(true);
								drawCharacter("http://s3.amazonaws.com/MinecraftSkins/" + user + ".png", 293, 170, loginSkin2Image);
							}
						}
						usernames.put(user, new UserPasswordInformation(pass));
					}
					this.usernameField.addItem(user);
				}
			} catch (EOFException ignored) {
			}
			dis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		updatePasswordField();
	}

	private void writeUsernameList() {
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");

			DataOutputStream dos;
			if (cipher != null)
				dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
			else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
			}
			for (String user : usernames.keySet()) {
				dos.writeUTF(user);
				UserPasswordInformation info = usernames.get(user);
				dos.writeBoolean(info.isHash);
				if (info.isHash) {
					dos.write(info.passwordHash);
				} else {
					dos.writeUTF(info.password);
				}
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent event) {
		String eventId = event.getActionCommand();
		if (event.getSource() == loginSkin1 || event.getSource() == loginSkin2) {
			eventId = "Login";
			this.usernameField.setSelectedItem(((JButton) event.getSource()).getText());
		} else if (loginSkin1Image.contains(event.getSource())) {
			eventId = "Login";
			this.usernameField.setSelectedItem(loginSkin1.getText());
		} else if (loginSkin2Image.contains(event.getSource())) {
			eventId = "Login";
			this.usernameField.setSelectedItem(loginSkin2.getText());
		}
		if ((eventId.equals("Login") || eventId.equals(usernameField.getSelectedItem())) && loginButton.isEnabled()) {
			doLogin();
		} else if (eventId.equals("Options")) {
			options.setVisible(true);
			options.setBounds((int) getBounds().getCenterX() - 250, (int) getBounds().getCenterY() - 75, 300, 250);
		} else if (eventId.equals("comboBoxChanged")) {
			updatePasswordField();
		}
	}

	private void updatePasswordField() {
		if (this.usernameField.getSelectedItem() != null) {
			UserPasswordInformation info = usernames.get(this.usernameField.getSelectedItem().toString());
			if (info.isHash) {
				this.passwordField.setText("");
				this.rememberCheckbox.setSelected(false);
			} else {
				this.passwordField.setText(info.password);
				this.rememberCheckbox.setSelected(true);
			}
		}
	}

	public void doLogin() {
		doLogin(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()), false);
	}

	public void doLogin(final boolean silentMode) {
		doLogin(usernameField.getSelectedItem().toString(), new String(passwordField.getPassword()), false, silentMode);
	}

	public void doLogin(final String user, final String pass) {
		doLogin(user, pass, true);
	}

	public void doLogin(final String user, final String pass, final boolean cmdLine) {
		doLogin(user, pass, cmdLine, false);
	}

	public void doLogin(final String user, final String pass, final boolean cmdLine, final boolean silentMode) {
		if (user == null || pass == null) {
			// JOptionPane.showMessageDialog(getParent(), "Incorrect username /password combination");
			return;
		}

		this.loginButton.setEnabled(false);
		this.optionsButton.setEnabled(false);
		this.loginSkin1.setEnabled(false);
		this.loginSkin2.setEnabled(false);
		options.setVisible(false);
		SwingWorker<Boolean, Boolean> loginThread = new SwingWorker<Boolean, Boolean>() {

			protected Boolean doInBackground() throws Exception {
				progressBar.setVisible(true);
				progressBar.setString("Connecting to www.minecraft.net...");
				try {
					values = MinecraftUtils.doLogin(user, pass, progressBar);
					return true;
				} catch (BadLoginException e) {
					JOptionPane.showMessageDialog(getParent(), "Incorrect usernameField/passwordField combination");
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (MinecraftUserNotPremiumException e) {
					JOptionPane.showMessageDialog(getParent(), "The specified account is not premium");
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (MCNetworkException e) {
					UserPasswordInformation info = null;
					for (String username : usernames.keySet()) {
						if (user.equalsIgnoreCase(username)) {
							info = usernames.get(username);
							break;
						}
					}
					boolean authFailed = (info == null);

					if (!authFailed) {
						if (info.isHash) {
							try {
								MessageDigest digest = MessageDigest.getInstance("SHA-256");
								byte[] hash = digest.digest(pass.getBytes());
								for (int i = 0; i < hash.length; i++) {
									if (hash[i] != info.passwordHash[i]) {
										authFailed = true;
										break;
									}
								}
							} catch (NoSuchAlgorithmException ex) {
								authFailed = true;
							}
						} else {
							authFailed = !(pass.equals(info.password));
						}
					}

					if (authFailed) {
						JOptionPane.showMessageDialog(getParent(), "Unable to authenticate account with minecraft.net");
					} else {
						int result = JOptionPane.showConfirmDialog(getParent(), "Would you like to run in offline mode?", "Unable to Connect to Minecraft.net", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							values = new String[] { "0", "0", user, "0" };
							return true;
						}
					}
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (OutdatedMCLauncherException e) {
					JOptionPane.showMessageDialog(getParent(), "The unthinkable has happened, alert dev@getspout.org!!!!");
					progressBar.setVisible(false);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					this.cancel(true);
					progressBar.setVisible(false);
				} catch (Exception e) {
				}
				loginButton.setEnabled(true);
				optionsButton.setEnabled(true);
				loginSkin1.setEnabled(true);
				loginSkin2.setEnabled(true);
				this.cancel(true);
				return false;
			}

			protected void done() {
				if (values == null || values.length < 4)
					return;
				LoginForm.pass = pass;

				MessageDigest digest = null;

				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
				}

				gu.user = values[2].trim();
				gu.downloadTicket = values[1].trim();
				gu.latestVersion = Long.parseLong(values[0].trim());
				if (settings.checkProperty("devupdate"))
					gu.devmode = settings.getPropertyBoolean("devupdate");
				if (cmdLine == false) {
					String password = new String(passwordField.getPassword());
					if (rememberCheckbox.isSelected()) {
						usernames.put(gu.user, new UserPasswordInformation(password));
					} else {
						if (digest == null) {
							usernames.put(gu.user, new UserPasswordInformation(""));
						} else {
							usernames.put(gu.user, new UserPasswordInformation(digest.digest(password.getBytes())));
						}
					}
					writeUsernameList();
				}

				SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {

					protected void done() {
						if (mcUpdate) {
							updateDialog.setToUpdate("Minecraft");
						} else if (spoutUpdate) {
							updateDialog.setToUpdate("Spoutcraft");
						}
						if (silentMode) {
							updateThread();
						} else if (mcUpdate || spoutUpdate) {
							LoginForm.updateDialog.setVisible(true);
						} else {
							runGame();
						}
						this.cancel(true);
					}

					protected Boolean doInBackground() throws Exception {
						publish("Checking for Minecraft Update...\n");
						try {
							mcUpdate = gu.checkMCUpdate(new File(GameUpdater.binDir + File.separator + "version"));
						} catch (Exception e) {
							mcUpdate = false;
						}

						publish("Checking for Spout update...\n");
						try {
							spoutUpdate = mcUpdate || gu.checkSpoutUpdate();
						} catch (Exception e) {
							spoutUpdate = false;
						}
						return true;
					}

					protected void process(List<String> chunks) {
						progressBar.setString(chunks.get(0));
					}
				};
				updateThread.execute();
				this.cancel(true);
			}
		};
		loginThread.execute();
	}

	public void updateThread() {
		SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {
			boolean error = false;

			protected void done() {
				progressBar.setVisible(false);
				if (!error) {
					runGame();
				}
				this.cancel(true);
			}

			protected Boolean doInBackground() throws Exception {
				try {
					if (mcUpdate) {
						gu.updateMC();
					}

					if (spoutUpdate) {
						gu.updateSpout();
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(getParent(), "Download Interrupted!");
					LauncherFrame.errorInDownload = true;
					error = true;
					loginButton.setEnabled(true);
					optionsButton.setEnabled(true);
					loginSkin1.setEnabled(true);
					loginSkin2.setEnabled(true);
					this.cancel(true);
					return false;
				}
				return true;
			}

			protected void process(List<String> chunks) {
				progressBar.setString(chunks.get(0));
			}

		};
		updateThread.execute();
	}

	private Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

	public void runGame() {
		LauncherFrame launcher = new LauncherFrame();
		launcher.setLoginForm(this);
		int result = launcher.runGame(values[2].trim(), values[3].trim(), values[1].trim(), pass);
		if (result == LauncherFrame.SUCCESSFUL_LAUNCH) {
			LoginForm.updateDialog.dispose();
			LoginForm.updateDialog = null;
			setVisible(false);
			dispose();
		} else if (result == LauncherFrame.ERROR_IN_LAUNCH) {
			loginButton.setEnabled(true);
			optionsButton.setEnabled(true);
			loginSkin1.setEnabled(true);
			loginSkin2.setEnabled(true);
			progressBar.setVisible(false);
		}
		// Do nothing for retrying launch
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		if (!LauncherFrame.successfulGameLaunch) {
			System.out.println("Exiting the Spoutcraft Launcher");
			System.exit(0);
		}
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}
}
