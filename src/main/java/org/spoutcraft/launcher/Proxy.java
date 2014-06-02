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

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class Proxy {
    private String host = null;
    private String port = null;
    private String user = null;
    private char[] pass = null;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(char[] pass) {
        this.pass = pass;
    }

    public void setup() {
        if (host != null) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("https.proxyHost", host);
            if (port != null) {
                System.setProperty("http.proxyPort", port);
                System.setProperty("https.proxyPort", port);
            }
        }
        if (user != null && pass != null) {
            Authenticator.setDefault(new ProxyAuthenticator(user, pass));
        }
    }

    private static class ProxyAuthenticator extends Authenticator {
        final String user;
        final char[] pass;

        ProxyAuthenticator(String user, char[] pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pass);
        }
    }
}
