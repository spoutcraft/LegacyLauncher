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

import net.technicpack.launchercore.install.AddPack;
import net.technicpack.launchercore.install.AvailablePackList;
import net.technicpack.launchercore.install.IPackListener;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.user.UserModel;
import net.technicpack.launchercore.mirror.MirrorStore;
import net.technicpack.launchercore.restful.solder.SolderPackInfo;
import net.technicpack.launchercore.util.Utils;
import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.skin.components.PackButton;
import org.spoutcraft.launcher.skin.options.ImportOptions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModpackSelector extends JComponent implements ActionListener, IPackListener {
    private static final long serialVersionUID = 1L;
    private static final String PACK_SELECT_ACTION = "packselect";
    private final LauncherFrame frame;
    private final List<PackButton> buttons = new ArrayList<PackButton>(7);
    private final int height = 520;
    private final int bigWidth = 180;
    private final int bigHeight = 110;
    private final float smallScale = 0.7F;
    private final int spacing = 8;
    private final int smallWidth = (int) (bigWidth * smallScale);
    private final int smallHeight = (int) (bigHeight * smallScale);
    private final int bigX = 100 - (bigWidth / 2);
    private final int bigY = (height / 2) - (bigHeight / 2);
    private final int smallX = 100 - (smallWidth / 2);
    private ImportOptions importOptions = null;

    private AvailablePackList mPackList;
    private UserModel mUserModel;
    private MirrorStore mirrorStore;

    public ModpackSelector(LauncherFrame frame, AvailablePackList packList, UserModel userModel, MirrorStore mirrorStore) {
        this.frame = frame;
        this.mPackList = packList;
        this.mUserModel = userModel;
        this.mirrorStore = mirrorStore;

        this.mPackList.addPackListener(this);

        for (int i = 0; i < 7; i++) {
            PackButton button = new PackButton();
            buttons.add(button);
            JLabel label = button.getJLabel();
            JLabel icon = button.getDisconnectedIcon();
            button.setActionCommand(PACK_SELECT_ACTION);
            button.addActionListener(this);
            if (i == 3) {
                button.setBounds(bigX, bigY, bigWidth, bigHeight);
                label.setBounds(bigX, bigY + bigHeight - 24, bigWidth, 24);
                icon.setBounds(bigX + bigWidth - 80, bigY, 80, 17);
                label.setFont(label.getFont().deriveFont(14F));
                button.setIndex(0);
            } else if (i < 3) {
                int smallY = bigY - (spacing * 2) - ((smallHeight + spacing) * (i + 1));
                button.setBounds(smallX, smallY, smallWidth, smallHeight);
                label.setBounds(smallX, smallY + smallHeight - 20, smallWidth, 20);
                icon.setBounds(smallX + smallWidth - 80, smallY, 80, 17);
                button.setIndex((i + 1) * -1);
            } else if (i > 3) {
                int smallY = bigY + bigHeight + ((smallHeight + spacing) * (i - 4)) + (spacing * 3);
                button.setBounds(smallX, smallY, smallWidth, smallHeight);
                label.setBounds(smallX, smallY + smallHeight - 20, smallWidth, 20);
                icon.setBounds(smallX + smallWidth - 80, smallY, 80, 17);
                button.setIndex(i - 3);
            }

            this.add(label);
            this.add(icon);
            this.add(button);
        }
    }

    public void addPack(InstalledPack pack) {
        mPackList.put(pack);
        selectPack(pack);
    }

    public void selectPack(InstalledPack pack) {
        System.out.println(pack);
        if (pack == null) {
            return;
        }

        mPackList.setPack(pack);
        redraw(pack, false);
    }

    public void updatePack(InstalledPack pack) {
        this.redraw(true);
    }

    public void redraw(InstalledPack selected, boolean force) {
        String packName = selected.getName();
        String displayName = selected.getDisplayName();
        // Determine if the pack is from the platform.
        boolean custom = selected.isPlatform();

        // Set the background image based on the pack
        frame.getBackgroundImage().changeBackground(packName, selected.getBackground(), force);

        // Set the icon image based on the pack
        frame.setIconImage(selected.getIcon());

        // Set the frame title based on the pack
        frame.setTitle(displayName);

        // Set the big button image in the middle
        buttons.get(3).setPack(selected);

        // Set the URL for the platform button
        String url = "http://www.technicpack.net/modpack/details/" + packName;
        if (selected.getInfo() instanceof SolderPackInfo && !custom) {
            String newUrl = selected.getInfo().getUrl();
            if (newUrl != null && !newUrl.isEmpty()) {
                url = newUrl;
                frame.enableComponent(frame.getPlatform(), true);
            } else {
                frame.enableComponent(frame.getPlatform(), false);
            }
        }
        frame.getPlatform().setURL(url);

        // Add the first 3 buttons to the left
        for (int i = 0; i < 3; i++) {
            InstalledPack pack = mPackList.getOffsetPack(-(i + 1));
            buttons.get(i).setPack(pack);
        }

        // Add the last 3 buttons to the right
        for (int i = 4; i < 7; i++) {
            InstalledPack pack = mPackList.getOffsetPack(i - 3);
            buttons.get(i).setPack(pack);
        }

        if (selected instanceof AddPack) {
            frame.enableComponent(frame.getPackOptionsBtn(), false);
            frame.enableComponent(frame.getPackRemoveBtn(), false);
            frame.enableComponent(frame.getPlatform(), false);
        } else if (custom) {
            frame.enableComponent(frame.getPackOptionsBtn(), true);
            frame.enableComponent(frame.getPackRemoveBtn(), true);
            frame.enableComponent(frame.getPlatform(), true);
        } else {
            frame.enableComponent(frame.getPackOptionsBtn(), true);
            frame.enableComponent(frame.getPackRemoveBtn(), false);
        }

        this.repaint();
    }

    public void removePack() {
        InstalledPack pack = mPackList.getOffsetPack(0);
        String packName = pack.getName();

        File file = pack.getInstalledDirectory();
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(Utils.getAssetsDirectory(), packName);
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mPackList.remove(pack);
        selectPack(mPackList.getOffsetPack(-1));
    }

    public InstalledPack getSelectedPack() {
        return mPackList.getOffsetPack(0);
    }

    public void redraw(boolean force) {
        redraw(getSelectedPack(), force);
    }

    public void selectNextPack() {
        selectPack(mPackList.getOffsetPack(1));
    }

    public void selectPreviousPack() {
        selectPack(mPackList.getOffsetPack(-1));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComponent) {
            action(e.getActionCommand(), (JComponent) e.getSource());
        }
    }

    public void action(String action, JComponent c) {
        if (action.equals(PACK_SELECT_ACTION) && c instanceof PackButton) {
            PackButton button = (PackButton) c;

            if (button.getIndex() == 0 && getSelectedPack() instanceof AddPack) {
                if (importOptions == null || !importOptions.isVisible()) {
                    importOptions = new ImportOptions(mPackList, mUserModel, mirrorStore);
                    importOptions.setModal(true);
                    importOptions.setVisible(true);
                }
            } else {
                selectPack(mPackList.getOffsetPack(button.getIndex()));
            }
        }
    }
}
