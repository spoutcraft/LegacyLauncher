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

import java.io.IOException;

public class DownloadException extends IOException {
	private final Throwable cause;
	private final String message;

	public DownloadException(String message, Throwable cause) {
		this.cause = cause;
		this.message = message;
	}

	public DownloadException(Throwable cause) {
		this(null, cause);
	}

	public DownloadException(String message) {
		this(message, null);
	}

	public DownloadException() {
		this(null, null);
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	@Override
	public String getMessage() {
		return message;
	}

	private static final long serialVersionUID = 1L;
}
