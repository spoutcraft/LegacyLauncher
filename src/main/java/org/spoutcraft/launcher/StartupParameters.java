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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.internal.Lists;

import java.util.List;
import java.util.logging.Logger;

public final class StartupParameters {
    @SuppressWarnings("unused")
    private final String[] args;
    @Parameter
    private List<String> parameters = Lists.newArrayList();
    @Parameter(names = {"-username", "-user", "-u"}, description = "Minecraft Username")
    private String user = null;
    @Parameter(names = {"-password", "-pass", "-p"}, description = "Minecraft Password")
    private String pass = null;
    @Parameter(names = {"-server", "-host", "-join", "-j", "-h", "-s"}, description = "Minecraft Server to join")
    private String server = null;
    @Parameter(names = {"-portable", "--portable", "-pmode", "-portable_mode", "-pm"}, description = "Portable Mode")
    private boolean portable = false;
    @Parameter(names = {"-debug", "--debug", "-verbose", "-v", "-d"}, description = "Debug mode")
    private boolean debug = false;
    @Parameter(names = {"-proxy_host"}, description = "HTTP Proxy Host")
    private String proxyHost = null;
    @Parameter(names = {"-proxy_port"}, description = "HTTP Proxy Port")
    private String proxyPort = null;
    @Parameter(names = {"-proxy_user"}, description = "HTTP Proxy Username")
    private String proxyUser = null;
    @Parameter(names = {"-proxy_password"}, description = "HTTP Proxy Password")
    private String proxyPassword = null;
    @Parameter(names = {"-console"}, description = "Shows the console window")
    private boolean console = false;
    @Parameter(names = {"-width"}, description = "Sets the width of the minecraft window to be fixed to this.")
    private int width = -1;
    @Parameter(names = {"-height"}, description = "Sets the height of the minecraft window to be fixed to this.")
    private int height = -1;
    @Parameter(names = {"-solder"}, description = "URL pointing towards the solder pack you want to force add to the launcher.")
    private String solder = null;
    @Parameter(names = {"-fullscreen"}, description = "Whether to launch minecraft in fullscreen mode.")
    private boolean fullscreen = false;

    public StartupParameters(String[] args) {
        this.args = args;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void logParameters(Logger log) {
        log.info("------------ Startup Parameters ------------");
        if (user != null) {
            log.info("Minecraft Username: " + user);
        }
        if (pass != null) {
            log.info("Minecraft Password exists");
        }
        if (server != null) {
            log.info("Minecraft Server: " + server);
        }
        if (portable) {
            log.info("Portable mode activated");
        }
        if (debug) {
            log.info("Debug mode activated");
        }
        if (proxyHost != null) {
            log.info("Proxy Host: " + proxyHost);
        }
        if (proxyPort != null) {
            log.info("Proxy Port: " + proxyPort);
        }
        if (proxyUser != null) {
            log.info("Proxy User exists");
        }
        if (proxyPassword != null) {
            log.info("Proxy Password exists");
        }
        if (console) {
            log.info("Console frame enabled");
        }
        if (width != -1) {
            log.info("Minecraft frame width: " + width);
        }
        if (height != -1) {
            log.info("Minecraft frame height: " + height);
        }
        if (solder != null) {
            log.info("Forced solder pack: " + solder);
        }
        log.info("--------- End of Startup Parameters ---------");
    }

    public boolean isPortable() {
        return portable;
    }

    public boolean isDebugMode() {
        return debug;
    }

    public boolean isConsole() {
        return console;
    }

    public void setupProxy() {
        Proxy proxy = new Proxy();
        proxy.setHost(this.proxyHost);
        proxy.setPort(this.proxyPort);
        proxy.setUser(this.proxyUser);
        proxy.setPass(proxyPassword != null ? this.proxyPassword.toCharArray() : null);
        proxy.setup();
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSolderPack() {
        return solder;
    }

    public boolean getFullscreen() {
        return fullscreen;
    }
}
