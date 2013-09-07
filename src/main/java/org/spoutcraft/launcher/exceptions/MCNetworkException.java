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

package org.spoutcraft.launcher.exceptions;

public class MCNetworkException extends Exception {
	private static final long serialVersionUID = 5521671148991031931L;
	private final Throwable cause;
	private final String message;

	public MCNetworkException(String message) {
		this(null, message);
	}

	public MCNetworkException(Throwable throwable, String message) {
		this.cause = null;
		this.message = message;
	}

	public MCNetworkException() {
		this(null, "Could not connect to minecraft.net");
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
