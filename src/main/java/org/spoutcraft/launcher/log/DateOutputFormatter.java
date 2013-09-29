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

package org.spoutcraft.launcher.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DateOutputFormatter extends Formatter {
	private final SimpleDateFormat date;

	public DateOutputFormatter(SimpleDateFormat date) {
		this.date = date;
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();

		builder.append(date.format(record.getMillis()));
		builder.append(" [");
		builder.append(record.getLevel().getLocalizedName().toUpperCase());
		builder.append("] ");
		builder.append(formatMessage(record));
		builder.append('\n');

		if (record.getThrown() != null) {
			StringWriter writer = new StringWriter();
			record.getThrown().printStackTrace(new PrintWriter(writer));
			builder.append(writer.toString());
		}

		return builder.toString();
	}
}
