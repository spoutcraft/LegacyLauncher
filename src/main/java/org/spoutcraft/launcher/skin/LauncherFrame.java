/*
 * This file is part of Technic Launcher.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.spoutcraft.launcher.skin;

import net.technicpack.launchercore.install.AvailablePackList;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.user.IAuthListener;
import net.technicpack.launchercore.install.user.User;
import net.technicpack.launchercore.install.user.UserModel;
import net.technicpack.launchercore.install.user.skins.ISkinListener;
import net.technicpack.launchercore.install.user.skins.SkinRepository;
import net.technicpack.launchercore.mirror.MirrorStore;
import net.technicpack.launchercore.util.DownloadListener;
import net.technicpack.launchercore.util.ImageUtils;
import net.technicpack.launchercore.util.ResourceUtils;
import org.spoutcraft.launcher.donor.DonorSite;
import org.spoutcraft.launcher.launcher.Launcher;
import org.spoutcraft.launcher.skin.components.*;
import org.spoutcraft.launcher.skin.options.LauncherOptions;
import org.spoutcraft.launcher.skin.options.ModpackOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;

import static net.technicpack.launchercore.util.ResourceUtils.getResourceAsStream;

public class LauncherFrame extends JFrame implements ActionListener, KeyListener, MouseWheelListener, DownloadListener, ISkinListener, IAuthListener {
    public static final Color TRANSPARENT = new Color(45, 45, 45, 160);
    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 880;
    private static final int FRAME_HEIGHT = 520;
    private static final String OPTIONS_ACTION = "options";
    private static final String PACK_OPTIONS_ACTION = "packoptions";
    private static final String PACK_REMOVE_ACTION = "packremove";
    private static final String EXIT_ACTION = "exit";
    private static final String PACK_LEFT_ACTION = "packleft";
    private static final String PACK_RIGHT_ACTION = "packright";
    private static final String LAUNCH_ACTION = "launch";
    private static final String LOGOUT = "logout";
    private static final int SPACING = 7;
    public static URL icon = LauncherFrame.class.getResource("/org/spoutcraft/launcher/resources/icon.png");
    private LiteProgressBar progressBar;
    private LauncherOptions launcherOptions = null;
    private ModpackOptions packOptions = null;
    private ModpackSelector packSelector;
    private BackgroundImage packBackground;
    private ImageButton packOptionsBtn;
    private ImageButton packRemoveBtn;
    private ImageHyperlinkButton platform;
    private JLabel customName;
    private LiteButton launch;
    private JLabel userHead;
    private JLabel loggedInMsg;
    private LiteButton logout;
    private RoundedBox barBox;
    private NewsComponent news;
    private long previous = 0L;
    private User currentUser = null;

    private SkinRepository mSkinRepo;
    private UserModel mUserModel;
    private AvailablePackList mPackList;
    private DonorSite mDonorSite;
    private MirrorStore mirrorStore;

    public LauncherFrame(SkinRepository skinRepo, UserModel userModel, AvailablePackList packList, DonorSite donorSite, MirrorStore mirrorStore) {
        this.mSkinRepo = skinRepo;
        this.mUserModel = userModel;
        this.mPackList = packList;
        this.mDonorSite = donorSite;
        this.mirrorStore = mirrorStore;

        this.mUserModel.addAuthListener(this);

        initComponents(packList);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        packBackground = new BackgroundImage(this, FRAME_WIDTH, FRAME_HEIGHT);
        this.addMouseListener(packBackground);
        this.addMouseMotionListener(packBackground);
        this.addMouseWheelListener(this);
        getContentPane().add(packBackground);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void skinReady(User user) {
    }

    public void faceReady(User user) {
        if (this.currentUser != null && this.currentUser.getUsername().equals(user.getUsername()))
            userHead.setIcon(new ImageIcon(ImageUtils.scaleImage(this.mSkinRepo.getFaceImage(user), 48, 48)));
    }

    private void initComponents(AvailablePackList packList) {
        Font minecraft = getMinecraftFont(12);
        Font ready = getMinecraftFont(10);

        // Launch button area
        RoundedBox launchArea = new RoundedBox(TRANSPARENT);
        launchArea.setBounds(605, 375, 265, 50);

        launch = new LiteButton("PLAY");
        launch.setFont(getMinecraftFont(20));
        launch.setBounds(launchArea.getX() + 5, launchArea.getY() + 5, launchArea.getWidth() - 10, launchArea.getHeight() - 10);
        launch.setActionCommand(LAUNCH_ACTION);
        launch.addActionListener(this);

        // User info area
        RoundedBox userArea = new RoundedBox(TRANSPARENT);
        userArea.setBounds(605, 430, 265, 75);

        userHead = new JLabel();
        userHead.setBounds(userArea.getX() + userArea.getWidth() - 69, userArea.getY() + 13, 48, 48);
        userHead.setIcon(new ImageIcon(ImageUtils.scaleImage(this.mSkinRepo.getDefaultFace(), 48, 48)));

        loggedInMsg = new JLabel("");
        loggedInMsg.setFont(minecraft);
        loggedInMsg.setHorizontalAlignment(SwingConstants.RIGHT);
        loggedInMsg.setHorizontalTextPosition(SwingConstants.RIGHT);
        loggedInMsg.setBounds(userArea.getX() + 5, userArea.getY() + 8, 185, 30);
        loggedInMsg.setForeground(Color.white);

        logout = new LiteButton("Log Out", new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), Color.white, Color.white, Color.white);
        logout.setFont(minecraft);
        logout.setOpaque(false);
        logout.setHorizontalAlignment(SwingConstants.RIGHT);
        logout.setHorizontalTextPosition(SwingConstants.RIGHT);
        logout.setForeground(Color.white);
        logout.setBounds(userArea.getX() + 133, userArea.getY() + 32, 60, 30);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setActionCommand(LOGOUT);
        logout.addActionListener(this);

        // Technic logo
        JLabel logo = new JLabel();
        ImageIcon logoIcon = new ImageIcon(ImageUtils.scaleWithAspectWidth(ResourceUtils.getImage("header.png"), 275));
        logo.setIcon(logoIcon);
        logo.setBounds(600, 6, logoIcon.getIconWidth(), logoIcon.getIconHeight());

        // Pack Selector Background
        JLabel selectorBackground = new JLabel();
        selectorBackground.setBounds(15, 0, 200, 520);
        selectorBackground.setBackground(TRANSPARENT);
        selectorBackground.setOpaque(true);

        // Pack Select Up
        ImageButton packUp = new ImageButton(ResourceUtils.getIcon("upButton.png", 65, 65));
        packUp.setBounds(-7, 0, 65, 65);
        packUp.setActionCommand(PACK_LEFT_ACTION);
        packUp.addActionListener(this);

        // Pack Select Down
        ImageButton packDown = new ImageButton(ResourceUtils.getIcon("downButton.png", 65, 65));
        packDown.setBounds(-7, FRAME_HEIGHT - 65, 65, 65);
        packDown.setActionCommand(PACK_RIGHT_ACTION);
        packDown.addActionListener(this);

        // Progress Bar Background box
        barBox = new RoundedBox(TRANSPARENT);
        barBox.setVisible(false);
        barBox.setBounds(605, 205, 265, 35);

        // Progress Bar
        progressBar = new LiteProgressBar(this);
        progressBar.setBounds(barBox.getX() + SPACING, barBox.getY() + SPACING, barBox.getWidth() - (SPACING * 2), barBox.getHeight() - (SPACING * 2));
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
        progressBar.setOpaque(true);
        progressBar.setFont(minecraft);

        // News Items
        news = new NewsComponent();
        news.setBounds(barBox.getX(), logo.getY() + logo.getHeight(), barBox.getWidth(), 100);

        // Link background box
        RoundedBox linkArea = new RoundedBox(TRANSPARENT);
        linkArea.setBounds(605, 250, 265, 120);

        int linkWidth = linkArea.getWidth() - (SPACING * 2);
        int linkHeight = (linkArea.getHeight() - (SPACING * 4)) / 3;

        // Browse link
        JButton browse = new ImageHyperlinkButton("http://www.technicpack.net");
        browse.setToolTipText("Get More Modpacks");
        browse.setBounds(linkArea.getX() + SPACING, linkArea.getY() + SPACING, linkWidth, linkHeight);
        browse.setIcon(ResourceUtils.getIcon("platformLinkButton.png"));
        browse.setRolloverIcon(ResourceUtils.getIcon("platformLinkButtonBright.png"));
        browse.setContentAreaFilled(false);
        browse.setBorderPainted(false);

        // Forums link
        JButton forums = new ImageHyperlinkButton("http://forums.technicpack.net/");
        forums.setToolTipText("Visit the forums");
        forums.setBounds(linkArea.getX() + SPACING, browse.getY() + browse.getHeight() + SPACING, linkWidth, linkHeight);
        forums.setIcon(ResourceUtils.getIcon("forumsLinkButton.png"));
        forums.setRolloverIcon(ResourceUtils.getIcon("forumsLinkButtonBright.png"));
        forums.setContentAreaFilled(false);
        forums.setBorderPainted(false);

        // Donate link
        JButton donate = new ImageHyperlinkButton("http://www.technicpack.net/donate/");
        donate.setToolTipText("Donate to the modders");
        donate.setBounds(linkArea.getX() + SPACING, forums.getY() + forums.getHeight() + SPACING, linkWidth, linkHeight);
        donate.setIcon(ResourceUtils.getIcon("donateLinkButton.png"));
        donate.setRolloverIcon(ResourceUtils.getIcon("donateLinkButtonBright.png"));
        donate.setContentAreaFilled(false);
        donate.setBorderPainted(false);

        // Options Button
        ImageButton options = new ImageButton(ResourceUtils.getIcon("gear.png", 28, 28), ResourceUtils.getIcon("gearInverted.png", 28, 28));
        options.setBounds(FRAME_WIDTH - 34 * 2, 6, 28, 28);
        options.setActionCommand(OPTIONS_ACTION);
        options.addActionListener(this);
        options.addKeyListener(this);

        // Pack Options Button
        packOptionsBtn = new ImageButton(ResourceUtils.getIcon("packOptions.png", 20, 20), ResourceUtils.getIcon("packOptionsInverted.png", 20, 20));
        packOptionsBtn.setBounds(25, FRAME_HEIGHT / 2 + 56, 20, 20);
        packOptionsBtn.setActionCommand(PACK_OPTIONS_ACTION);
        packOptionsBtn.addActionListener(this);

        // Platform website button
        platform = new ImageHyperlinkButton("http://www.technicpack.net/");
        platform.setIcon(ResourceUtils.getIcon("openPlatformPage.png", 20, 20));
        platform.setBounds(50, FRAME_HEIGHT / 2 + 56, 20, 20);

        // Pack Remove Button
        packRemoveBtn = new ImageButton(ResourceUtils.getIcon("packDelete.png", 20, 20), ResourceUtils.getIcon("packDeleteInverted.png", 20, 20));
        packRemoveBtn.setBounds(185, FRAME_HEIGHT / 2 + 56, 20, 20);
        packRemoveBtn.setActionCommand(PACK_REMOVE_ACTION);
        packRemoveBtn.addActionListener(this);

        // Exit Button
        ImageButton exit = new ImageButton(ResourceUtils.getIcon("quit.png", 28, 28), ResourceUtils.getIcon("quitHover.png", 28, 28));
        exit.setBounds(FRAME_WIDTH - 34, 6, 28, 28);
        exit.setActionCommand(EXIT_ACTION);
        exit.addActionListener(this);

        // Steam button
        JButton steam = new ImageHyperlinkButton("http://steamcommunity.com/groups/technic-pack");
        steam.setRolloverIcon(ResourceUtils.getIcon("steamInverted.png", 28, 28));
        steam.setToolTipText("Game with us on Steam");
        steam.setBounds(215 + 6, 6, 28, 28);
        setIcon(steam, "steam.png", 28);

        // Twitter button
        JButton twitter = new ImageHyperlinkButton("https://twitter.com/TechnicPack");
        twitter.setRolloverIcon(ResourceUtils.getIcon("twitterInverted.png", 28, 28));
        twitter.setToolTipText("Follow us on Twitter");
        twitter.setBounds(215 + 6 + 34 * 3, 6, 28, 28);
        setIcon(twitter, "twitter.png", 28);

        // Facebook button
        JButton facebook = new ImageHyperlinkButton("https://www.facebook.com/TechnicPack");
        facebook.setRolloverIcon(ResourceUtils.getIcon("facebookInverted.png", 28, 28));
        facebook.setToolTipText("Like us on Facebook");
        facebook.setBounds(215 + 6 + 34 * 2, 6, 28, 28);
        setIcon(facebook, "facebook.png", 28);

        // YouTube button
        JButton youtube = new ImageHyperlinkButton("http://www.youtube.com/user/kakermix");
        youtube.setRolloverIcon(ResourceUtils.getIcon("youtubeInverted.png", 28, 28));
        youtube.setToolTipText("Subscribe to our videos");
        youtube.setBounds(215 + 6 + 34, 6, 28, 28);
        setIcon(youtube, "youtube.png", 28);

        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        // Pack Selector
        packSelector = new ModpackSelector(this, packList, mUserModel, mirrorStore);
        packSelector.setBounds(15, 0, 200, 520);

        // Custom Pack Name Label
        customName = new JLabel("", JLabel.CENTER);
        customName.setBounds(FRAME_WIDTH / 2 - (192 / 2), FRAME_HEIGHT / 2 + (110 / 2) - 30, 192, 30);
        customName.setFont(minecraft.deriveFont(14F));
        customName.setVisible(false);
        customName.setForeground(Color.white);

        contentPane.add(launch);
        contentPane.add(launchArea);
        contentPane.add(userHead);
        contentPane.add(loggedInMsg);
        contentPane.add(logout);
        contentPane.add(userArea);
        contentPane.add(progressBar);
        contentPane.add(barBox);
        contentPane.add(packUp);
        contentPane.add(packDown);
        contentPane.add(customName);
        contentPane.add(packOptionsBtn);
        contentPane.add(packRemoveBtn);
        contentPane.add(platform);
        contentPane.add(packSelector);
        contentPane.add(selectorBackground);
        contentPane.add(steam);
        contentPane.add(twitter);
        contentPane.add(facebook);
        contentPane.add(youtube);
        contentPane.add(browse);
        contentPane.add(forums);
        contentPane.add(donate);
        contentPane.add(linkArea);
        contentPane.add(logo);
        contentPane.add(news);
        contentPane.add(options);
        contentPane.add(exit);
    }

    private void setIcon(JButton button, String iconName, int size) {
        try {
            button.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), size, size)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Font getMinecraftFont(int size) {
        Font minecraft;
        try {
            minecraft = Font.createFont(Font.TRUETYPE_FONT, getResourceAsStream("/org/spoutcraft/launcher/resources/minecraft.ttf")).deriveFont((float) size);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback
            minecraft = new Font("Arial", Font.PLAIN, 12);
        }
        return minecraft;
    }

    public static void setIcon(JLabel label, String iconName, int w, int h) {
        try {
            label.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public NewsComponent getNews() {
        return news;
    }

    public BackgroundImage getBackgroundImage() {
        return packBackground;
    }

    public RoundedBox getBarBox() {
        return barBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComponent) {
            action(e.getActionCommand());
        }
    }

    private void action(String action) {
        if (action.equals(OPTIONS_ACTION)) {
            if (launcherOptions == null || !launcherOptions.isVisible()) {
                launcherOptions = new LauncherOptions();
                launcherOptions.setModal(true);
                launcherOptions.setVisible(true);
            }
        } else if (action.equals(PACK_REMOVE_ACTION)) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this pack?\n This will delete all files in: " + getSelector().getSelectedPack().getInstalledDirectory(), "Remove Pack", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                getSelector().removePack();
            }
        } else if (action.equals(PACK_OPTIONS_ACTION)) {
            if (getSelector().getSelectedPack().getInfo() != null && (packOptions == null || !packOptions.isVisible())) {
                System.out.println("Opening options for " + getSelector().getSelectedPack());
                packOptions = new ModpackOptions(getSelector().getSelectedPack(), mPackList);
                packOptions.setModal(true);
                packOptions.setVisible(true);
            }
        } else if (action.equals(EXIT_ACTION)) {
            System.exit(0);
        } else if (action.equals(PACK_LEFT_ACTION)) {
            getSelector().selectPreviousPack();
        } else if (action.equals(PACK_RIGHT_ACTION)) {
            getSelector().selectNextPack();
        } else if (action.equals(LAUNCH_ACTION)) {
            if (Launcher.isLaunching()) {
                return;
            }

            InstalledPack pack = packSelector.getSelectedPack();

            if (!pack.getName().equals("addpack") && (pack.isLocalOnly() || pack.getInfo() != null)) {
                Launcher.launch(currentUser, pack, pack.getBuild());
            }
        } else if (action.equals(LOGOUT)) {
            if (Launcher.isLaunching()) {
                return;
            }

            mUserModel.setCurrentUser(null);
        }
    }

    public ModpackSelector getSelector() {
        return packSelector;
    }

    @Override
    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
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

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void disableForm() {
    }

    public void enableForm() {
        progressBar.setVisible(false);
        lockLoginButton(true);
    }

    public void lockLoginButton(boolean unlock) {
        if (unlock) {
            if (currentUser != null && currentUser.isOffline())
                launch.setText("PLAY OFFLINE");
            else
                launch.setText("PLAY");
        } else {
            launch.setText("LAUNCHING...");
        }
        launch.setEnabled(unlock);
        packRemoveBtn.setEnabled(unlock);
        packOptionsBtn.setEnabled(unlock);
    }

    public ImageButton getPackOptionsBtn() {
        return packOptionsBtn;
    }

    public ImageButton getPackRemoveBtn() {
        return packRemoveBtn;
    }

    public ImageHyperlinkButton getPlatform() {
        return platform;
    }

    public void enableComponent(JComponent component, boolean enable) {
        component.setVisible(enable);
        component.setEnabled(enable);
    }

    public void userChanged(User user) {
        this.currentUser = user;

        if (user == null) {
            this.setVisible(false);
            return;
        }

        if (currentUser.isOffline())
            launch.setText("PLAY OFFLINE");
        else {
            launch.setText("PLAY");
            mUserModel.setLastUser(currentUser);

//			if (mDonorSite.doesUserQualify(1, currentUser.getProfile().getName(), 5))
//				this.bteamArea.setVisible(true);
        }

        loggedInMsg.setText(currentUser.getDisplayName());

        this.faceReady(currentUser);
        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWhen() != previous) {
            if (e.getUnitsToScroll() > 0) {
                getSelector().selectNextPack();
            } else if (e.getUnitsToScroll() < 0) {
                getSelector().selectPreviousPack();
            }
            this.previous = e.getWhen();
        }

    }
}
