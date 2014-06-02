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

package org.spoutcraft.launcher;

import net.technicpack.launchercore.exception.BuildInaccessibleException;
import net.technicpack.launchercore.exception.CacheDeleteException;
import net.technicpack.launchercore.exception.DownloadException;
import net.technicpack.launchercore.exception.PackNotAvailableOfflineException;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.ModpackInstaller;
import net.technicpack.launchercore.install.user.UserModel;
import net.technicpack.launchercore.launch.LaunchOptions;
import net.technicpack.launchercore.launch.MinecraftLauncher;
import net.technicpack.launchercore.minecraft.CompleteVersion;
import net.technicpack.launchercore.mirror.MirrorStore;
import net.technicpack.launchercore.util.LaunchAction;
import net.technicpack.launchercore.util.Settings;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.launcher.Launcher;

import javax.swing.*;
import java.io.IOException;
import java.util.zip.ZipException;

public class InstallThread extends Thread {
    private final InstalledPack pack;
    private final ModpackInstaller modpackInstaller;
    private final UserModel userModel;
    private final MirrorStore mirrorStore;
    private boolean finished = false;

    public InstallThread(InstalledPack pack, String build, UserModel userModel, MirrorStore mirrorStore) {
        super("InstallThread");
        this.pack = pack;
        this.modpackInstaller = new ModpackInstaller(Launcher.getFrame(), pack, build, mirrorStore);
        this.userModel = userModel;
        this.mirrorStore = mirrorStore;
    }

    @Override
    public void run() {
        try {
            Launcher.getFrame().getProgressBar().setVisible(true);
            CompleteVersion version = null;
            if (!pack.isLocalOnly()) {
                version = modpackInstaller.installPack(Launcher.getFrame(), userModel.getCurrentUser());
            } else {
                version = modpackInstaller.prepareOfflinePack();
            }

            int memory = Memory.getMemoryFromId(Settings.getMemory()).getMemoryMB();
            MinecraftLauncher minecraftLauncher = new MinecraftLauncher(memory, pack, version);

            StartupParameters params = SpoutcraftLauncher.params;
            LaunchOptions options = new LaunchOptions(pack.getDisplayName(), pack.getIconPath(), params.getWidth(), params.getHeight(), params.getFullscreen());
            LauncherUnhider unhider = new LauncherUnhider();
            minecraftLauncher.launch(userModel.getCurrentUser(), options, unhider, mirrorStore);

            LaunchAction launchAction = Settings.getLaunchAction();

            if (launchAction == null || launchAction == LaunchAction.HIDE) {
                Launcher.getFrame().setVisible(false);
            } else if (launchAction == LaunchAction.CLOSE) {
                System.exit(0);
            }
        } catch (PackNotAvailableOfflineException e) {
            JOptionPane.showMessageDialog(Launcher.getFrame(), e.getMessage(), "Cannot Start Modpack", JOptionPane.WARNING_MESSAGE);
        } catch (DownloadException e) {
            JOptionPane.showMessageDialog(Launcher.getFrame(), "Error downloading file for the following pack: " + pack.getDisplayName() + " \n\n" + e.getMessage() + "\n\nPlease consult the modpack author.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (ZipException e) {
            JOptionPane.showMessageDialog(Launcher.getFrame(), "Error unzipping a file for the following pack: " + pack.getDisplayName() + " \n\n" + e.getMessage() + "\n\nPlease consult the modpack author.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (CacheDeleteException e) {
            JOptionPane.showMessageDialog(Launcher.getFrame(), "Error installing the following pack: " + pack.getDisplayName() + " \n\n" + e.getMessage() + "\n\nPlease check your system settings.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (BuildInaccessibleException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Launcher.getFrame(), e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Launcher.getFrame().getProgressBar().setVisible(false);

            finished = true;
        }
    }

    public boolean isFinished() {
        return modpackInstaller.isFinished() || finished;
    }
}
