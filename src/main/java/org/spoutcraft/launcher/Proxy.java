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

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pass);
		}
	}
}
