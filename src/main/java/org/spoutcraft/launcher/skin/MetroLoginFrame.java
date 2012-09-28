/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spoutcraft is licensed under the SpoutDev License Version 1.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
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
package org.spoutcraft.launcher.skin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.spoutcraft.launcher.skin.components.BackgroundImage;
import org.spoutcraft.launcher.skin.components.DynamicButton;
import org.spoutcraft.launcher.skin.components.HyperlinkJLabel;
import org.spoutcraft.launcher.skin.components.ImageHyperlinkButton;
import org.spoutcraft.launcher.skin.components.LiteTextBox;
import org.spoutcraft.launcher.skin.components.LoginFrame;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.ResourceUtils;

public class MetroLoginFrame extends LoginFrame implements WindowListener, ActionListener, KeyListener{
	private static final long serialVersionUID = 1L;
	private static final URL gearIcon = LegacyLoginFrame.class.getResource("/org/spoutcraft/launcher/resources/gear_icon.png");
	private static final int FRAME_WIDTH = 880;
	private static final int FRAME_HEIGHT = 520;
	private DynamicButton user;
	private LiteTextBox name;
	private LiteTextBox pass;
	public MetroLoginFrame() {
		initComponents();
		this.addWindowListener(this);
		name.addKeyListener(this);
		pass.addKeyListener(this);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		getContentPane().add(new BackgroundImage(FRAME_WIDTH, FRAME_HEIGHT));
	}

	private void initComponents() {
		user = new DynamicButton(getImage("Afforess"), 44);
		name = new LiteTextBox(this, "Username...");
		name.setBounds(622, 426, 140, 24);
		pass = new LiteTextBox(this, "Password...");
		pass.setBounds(622, 455, 140, 24);
		Font minecraft = getMinecraftFont(12);
		name.setFont(minecraft);
		pass.setFont(minecraft);
		
		//Giant Logo
		JLabel logo = new JLabel();
		logo.setBounds(8, 15, 400, 109);
		setIcon(logo, "spoutcraft.png", logo.getWidth(), logo.getHeight());
		
		//Home
		HyperlinkJLabel home = new HyperlinkJLabel("Home", "http://www.spout.org/");
		home.setFont(minecraft.deriveFont((float)20));
		home.setBounds(545, 35, 65, 20);
		home.setForeground(Color.WHITE);
		home.setOpaque(false);
		home.setTransparency(0.70F);
		home.setHoverTransparency(1F);
		
		//Forums
		HyperlinkJLabel forums = new HyperlinkJLabel("Forums", "http://forums.spout.org/");
		forums.setFont(minecraft.deriveFont((float)20));
		forums.setBounds(625, 35, 90, 20);
		forums.setForeground(Color.WHITE);
		forums.setOpaque(false);
		forums.setTransparency(0.70F);
		forums.setHoverTransparency(1F);
		
		//Issues
		HyperlinkJLabel issues = new HyperlinkJLabel("Issues", "http://spout.in/issues");
		issues.setFont(minecraft.deriveFont((float)20));
		issues.setBounds(733, 35, 85, 20);
		issues.setForeground(Color.WHITE);
		issues.setOpaque(false);
		issues.setTransparency(0.70F);
		issues.setHoverTransparency(1F);
		
		//Steam
		JButton steam = new ImageHyperlinkButton("http://spout.in/steam");
		steam.setToolTipText("Game with us on Steam");
		steam.setBounds(6, FRAME_HEIGHT - 62, 28, 28);
		setIcon(steam, "steam_48x48.png", 28);
		
		//Youtube
		JButton youtube = new ImageHyperlinkButton("http://spout.in/youtube");
		youtube.setToolTipText("Subscribe to our videos");
		youtube.setBounds(6 + 34, FRAME_HEIGHT - 62, 28, 28);
		setIcon(youtube, "youtube_48x48.png", 28);
		
		//G+
		JButton googlePlus = new ImageHyperlinkButton("http://spout.in/googleplus");
		googlePlus.setToolTipText("Follow us on Google+");
		googlePlus.setBounds(6 + 34 * 2, FRAME_HEIGHT - 62, 28, 28);
		setIcon(googlePlus, "gplus_48x48.png", 28);
		
		//Facebook
		JButton facebook = new ImageHyperlinkButton("http://spout.in/facebook");
		facebook.setToolTipText("Like us on Facebook");
		facebook.setBounds(6 + 34 * 3, FRAME_HEIGHT - 62, 28, 28);
		setIcon(facebook, "facebook_48x48.png", 28);
		
		//Twitter
		JButton twitter = new ImageHyperlinkButton("http://spout.in/twitter");
		twitter.setToolTipText("Follow us on Twitter");
		twitter.setBounds(6 + 34 * 4, FRAME_HEIGHT - 62, 28, 28);
		setIcon(twitter, "twitter_48x48.png", 28);
		
		//Paypal
		JButton paypal = new ImageHyperlinkButton("http://forums.spout.org/account/upgrades");
		paypal.setToolTipText("Donate to the Spout project");
		paypal.setBounds(6 + 34 * 5, FRAME_HEIGHT - 62, 28, 28);
		setIcon(paypal, "paypal_48x48.png", 28);

		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		contentPane.add(user);
		contentPane.add(name);
		contentPane.add(pass);
		contentPane.add(steam);
		contentPane.add(youtube);
		contentPane.add(googlePlus);
		contentPane.add(facebook);
		contentPane.add(twitter);
		contentPane.add(paypal);
		contentPane.add(home);
		contentPane.add(forums);
		contentPane.add(issues);
		contentPane.add(logo);
		user.setBounds(300, 200, 75, 75);
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

	private BufferedImage getImage(String user){
		try {
			URLConnection conn = (new URL("https://minotar.net/avatar/" + user + "/100")).openConnection();
			InputStream stream = conn.getInputStream();
			BufferedImage image = ImageIO.read(stream);
			return image;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
	}

	public void stateChanged(String fileName, float progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JProgressBar getProgressBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disableForm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enableForm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSelectedUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		int dx = 0, dy = 0;
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			dx--;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			dx++;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			dy--;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			dy++;
		}
		JComponent c = (JComponent) e.getComponent();
		c.setBounds(c.getX() + dx, c.getY() + dy, c.getWidth(), c.getHeight());
		System.out.println("Icon pos: " + c.getX() + ", " + c.getY());
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
