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

package org.spoutcraft.launcher.launcher;

import net.technicpack.launchercore.exception.AuthenticationNetworkFailureException;
import net.technicpack.launchercore.install.AvailablePackList;
import net.technicpack.launchercore.install.IPackStore;
import net.technicpack.launchercore.install.InstalledPack;
import net.technicpack.launchercore.install.user.User;
import net.technicpack.launchercore.install.user.UserModel;
import net.technicpack.launchercore.install.user.skins.MinotarSkinStore;
import net.technicpack.launchercore.install.user.skins.SkinRepository;
import net.technicpack.launchercore.mirror.MirrorStore;
import net.technicpack.launchercore.mirror.secure.rest.JsonWebSecureMirror;
import net.technicpack.launchercore.util.Utils;
import org.spoutcraft.launcher.InstallThread;
import org.spoutcraft.launcher.donor.DonorSite;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.skin.LauncherFrame;
import org.spoutcraft.launcher.skin.LoginFrame;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;

public class Launcher {
    private static Launcher instance;
    private final LauncherFrame launcherFrame;
    private final LoginFrame loginFrame;
    private final UserModel userModel;
    private final MirrorStore mirrorStore;
    private InstallThread installThread;

    private LinkedList<Thread> startupTasks = new LinkedList<Thread>();

    public Launcher() {
        if (Launcher.instance != null) {
            throw new IllegalArgumentException("You can't have a duplicate launcher");
        }

        userModel = new UserModel(Users.load());

        mirrorStore = new MirrorStore(userModel);
        mirrorStore.addSecureMirror("mirror.technicpack.net", new JsonWebSecureMirror("http://mirror.technicpack.net/", "mirror.technicpack.net"));

        SkinRepository skinRepo = new SkinRepository(new TechnicSkinMapper(), new MinotarSkinStore("https://minotar.net/", mirrorStore));

        instance = this;

        trackLauncher();

        IPackStore installedPacks = InstalledPacks.load(mirrorStore);
        AvailablePackList packList = new AvailablePackList(installedPacks, mirrorStore);
        userModel.addAuthListener(packList);

        DonorSite donors = new DonorSite("http://donate.technicpack.net/");

        this.launcherFrame = new LauncherFrame(skinRepo, userModel, packList, donors, mirrorStore);
        this.loginFrame = new LoginFrame(skinRepo, userModel);

        Thread news = new Thread("News Thread") {
            @Override
            public void run() {
                launcherFrame.getNews().loadArticles();
            }
        };

        news.start();
    }

    public void startup() {
        try {
            UserModel.AuthError error = userModel.AttemptLastUserRefresh();

            if (error != null) {
                userModel.setCurrentUser(null);
            }
        } catch (AuthenticationNetworkFailureException ex) {
            userModel.setCurrentUser(new User(userModel.getLastUser().getDisplayName()));
        }
    }

    public static Launcher getInstance() {
        return instance;
    }

    public static void launch(User user, InstalledPack pack, String build) {
        instance.installThread = new InstallThread(pack, build, instance.userModel, instance.mirrorStore);
        instance.installThread.start();
    }

    public static boolean isLaunching() {
        return instance.installThread != null && !instance.installThread.isFinished();
    }

    public static LauncherFrame getFrame() {
        return instance.launcherFrame;
    }

    public static LoginFrame getLoginFrame() {
        return instance.loginFrame;
    }

    public static void trackLauncher() {
        File installed = new File(Utils.getSettingsDirectory(), "installed");
        if (!installed.exists()) {
            try {
                installed.createNewFile();
                Utils.sendTracking("installLauncher", "install", SpoutcraftLauncher.getLauncherBuild());
            } catch (IOException e) {
                e.printStackTrace();
                Utils.getLogger().log(Level.INFO, "Failed to create install tracking file");
            }

        }

        Utils.sendTracking("runLauncher", "run", SpoutcraftLauncher.getLauncherBuild());
    }
}
